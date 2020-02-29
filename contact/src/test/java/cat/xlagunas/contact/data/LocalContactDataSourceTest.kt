package cat.xlagunas.contact.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import cat.xlagunas.core.data.converter.FriendConverter
import cat.xlagunas.core.data.db.FriendDao
import cat.xlagunas.core.data.db.UserDao
import cat.xlagunas.core.data.db.UserEntity
import cat.xlagunas.core.data.db.VivDatabase
import cat.xlagunas.core.domain.auth.AuthDataStore
import cat.xlagunas.core.domain.entity.Friend
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class LocalContactDataSourceTest {

    private lateinit var localContactDataSource: LocalContactDataSource

    private lateinit var userDao: UserDao

    private lateinit var friendDao: FriendDao

    private lateinit var vivDatabase: VivDatabase

    private lateinit var userEntity: UserEntity

    private val friendConverter = FriendConverter()

    @Mock
    private lateinit var authDataStore: AuthDataStore

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        vivDatabase = Room.inMemoryDatabaseBuilder(RuntimeEnvironment.application, VivDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val fakeUserId = 1L
        userEntity = UserEntity(fakeUserId, "username", "firstname", "lastname", "email", "url")
        `when`(authDataStore.getCurrentUserId()).thenReturn(fakeUserId)
        friendDao = vivDatabase.friendDao()
        userDao = vivDatabase.userDao()
        userDao.insert(userEntity)
        localContactDataSource = LocalContactDataSource(friendDao, authDataStore, friendConverter)
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
        assert(oldFriendStatus.relationshipStatus == cat.xlagunas.core.data.net.Relationship.NONE.name)

        localContactDataSource.requestFriendship(oldFriendStatus).test()

        val contact = localContactDataSource.getContacts().blockingFirst()

        assertThat(contact.first().relationshipStatus).isEqualToIgnoringCase(cat.xlagunas.core.data.net.Relationship.REQUESTED.name)
    }

    private fun generateFriend() = Friend(10, "aContact", "aName", "anImage", "anEmail", "NONE")
}