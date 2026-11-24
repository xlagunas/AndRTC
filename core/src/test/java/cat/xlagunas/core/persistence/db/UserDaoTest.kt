package cat.xlagunas.core.persistence.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class UserDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: VivDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setUp() {
        database =
            Room.inMemoryDatabaseBuilder(
                RuntimeEnvironment.getApplication(),
                VivDatabase::class.java,
            ).allowMainThreadQueries().build()
        userDao = database.userDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenUserInsertedThenDeleted_whenObservingUserCount_thenEmitsZeroOneZero() {
        val user = UserEntity(10, "fakeUser", "Name", "Surname", "email@email.com", null)
        val observer = userDao.getUserCount().test()

        userDao.insert(user)
        userDao.delete(user)

        observer.assertValues(0, 1, 0)
        observer.assertNotComplete()
    }
}
