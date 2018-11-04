package cat.xlagunas.data.contact

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import cat.xlagunas.data.BuildConfig
import cat.xlagunas.data.common.converter.FriendConverter
import cat.xlagunas.data.common.db.FriendDao
import cat.xlagunas.data.common.db.UserDao
import cat.xlagunas.data.common.db.UserEntity
import cat.xlagunas.data.common.db.VivDatabase
import cat.xlagunas.data.common.net.Relationship
import cat.xlagunas.domain.commons.Friend
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [26])
class LocalContactDataSourceTest {

    private lateinit var localContactDataSource: LocalContactDataSource

    private lateinit var userDao: UserDao

    private lateinit var friendDao: FriendDao

    private lateinit var vivDatabase: VivDatabase

    private lateinit var userEntity: UserEntity

    private val friendConverter = FriendConverter()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        vivDatabase = Room.inMemoryDatabaseBuilder(RuntimeEnvironment.application, VivDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        userEntity = UserEntity(1, "username", "firstname", "lastname", "email", "url")

        friendDao = vivDatabase.friendDao()
        userDao = vivDatabase.userDao()
        userDao.insert(userEntity)
        localContactDataSource = LocalContactDataSource(friendDao, userDao, friendConverter)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        vivDatabase.close()
    }

    @Test
    fun whenRequestFriendship_ThenFriendPersisted() {
        val expectedFriend = generateFriend()

        localContactDataSource
            .requestFriendship(expectedFriend).test()
            .assertComplete()

        val value = friendDao.getUserFriends(1)
            .test()
            .assertNotComplete()
            .assertValueCount(1).values().first()

        assertThat(friendConverter.toFriend(value.first()))
            .isEqualToIgnoringGivenFields(expectedFriend, "relationshipStatus")
    }

    @Test
    fun givenEmptyContactList_whenNewContact_thenListRefreshed() {
        val expectedFriend = generateFriend()
        val reactiveList = localContactDataSource.getContacts().test()
        reactiveList.assertValueCount(1)
        reactiveList.assertValue(emptyList()).assertNotComplete()

        localContactDataSource.requestFriendship(expectedFriend).test()

        reactiveList.assertValueCount(2)
        val resultFriendList = reactiveList.values().last()
        assertThat(resultFriendList)
            .usingElementComparatorIgnoringFields("relationshipStatus")
            .isEqualTo(listOf(expectedFriend))
    }

    @Test
    fun givenExistingContactList_whenUpdated_thenListRefreshed() {
        val oldFriend = generateFriend()
        localContactDataSource.requestFriendship(oldFriend).test()

        val friends = arrayListOf(oldFriend, oldFriend.copy(friendId = 2, name = "newFriendName"))
        val analisedObservable = localContactDataSource.getContacts().test()
        analisedObservable.assertValueCount(1)
        localContactDataSource.updateContacts(friends).test()

        analisedObservable.assertValueCount(2).assertValueAt(1, friends.reversed())
    }

    @Test
    fun givenNonExistingContact_whenAdded_thenRelationshipMatchesPending() {
        val oldFriendStatus = generateFriend()
        assert(oldFriendStatus.relationshipStatus == Relationship.NONE.name)

        localContactDataSource.requestFriendship(oldFriendStatus).test()

        val contact = localContactDataSource.getContacts().blockingFirst()

        assertThat(contact.first().relationshipStatus).isEqualToIgnoringCase(Relationship.REQUESTED.name)
    }

    private fun generateFriend() = Friend(10, "aContact", "aName", "anImage", "anEmail", "NONE")
}