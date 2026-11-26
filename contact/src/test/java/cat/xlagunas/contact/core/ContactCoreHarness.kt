package cat.xlagunas.contact.core

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import cat.xlagunas.contact.data.ContactCacheImpl
import cat.xlagunas.contact.data.ContactRepositoryImpl
import cat.xlagunas.contact.data.ContactsApi
import cat.xlagunas.contact.data.FriendConverter
import cat.xlagunas.contact.data.LocalContactDataSource
import cat.xlagunas.contact.data.RemoteContactDataSource
import cat.xlagunas.contact.ui.ContactViewModel
import cat.xlagunas.core.network.HttpClientFactory
import cat.xlagunas.core.persistence.AuthDataStore
import cat.xlagunas.core.persistence.db.FriendEntity
import cat.xlagunas.core.persistence.db.UserEntity
import cat.xlagunas.core.persistence.db.VivDatabase
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.RecordingNavigator
import cat.xlagunas.test.utils.StateRecorder
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.user.auth.AuthHeaderInterceptor
import cat.xlagunas.user.auth.AuthPreferenceDataStore
import com.google.gson.Gson

/**
 * The contact feature wired by hand with its production classes. Only HTTP ([FakeBackend]),
 * navigation and the clock are fakes.
 */
class ContactCoreHarness(
    backend: FakeBackend,
    rule: CoreTestRule,
) {
    private val context: Context = ApplicationProvider.getApplicationContext()

    val navigator = RecordingNavigator()
    val time = FakeTimeProvider()

    // Same preferences file as ApplicationModule.provideSharedPreference.
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    val dataStore: AuthDataStore = AuthPreferenceDataStore(sharedPreferences)

    // Trampoline schedulers run some Room queries on the test (main) thread.
    val database: VivDatabase =
        Room.inMemoryDatabaseBuilder(context, VivDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    private val finishedCalls = StateRecorder<Unit>()

    private val client =
        HttpClientFactory.okHttpClient(listOf(AuthHeaderInterceptor(dataStore))).also { client ->
            // Retrofit's async call adapter runs the whole Rx chain inside the OkHttp callback and
            // OkHttp reports the call as finished after it returns, so an idle event means the
            // action has completed.
            client.dispatcher.idleCallback = Runnable { finishedCalls.record(Unit) }
        }

    private val retrofit = HttpClientFactory.retrofit(backend.url, client, Gson())

    private val converter = FriendConverter()
    private val remote = RemoteContactDataSource(retrofit.create(ContactsApi::class.java), converter)
    private val local = LocalContactDataSource(database.friendDao(), dataStore, converter)
    private val cache = ContactCacheImpl(sharedPreferences, time)
    private val repository = ContactRepositoryImpl(local, remote, cache, rule.rxSchedulers)

    fun contactViewModel() = ContactViewModel(repository, dataStore, navigator)

    fun givenLoggedInUser(
        id: Long = 3,
        token: String = "token-123",
    ) {
        givenUser(id, "anna")
        dataStore.insertAuthToken(token)
        dataStore.updateCurrentUserId(id)
    }

    fun givenUser(
        id: Long,
        username: String,
    ) {
        database.userDao().insert(UserEntity(id, username, "Anna", "Puig", "$username@example.com", null))
    }

    fun givenStoredFriends(
        userId: Long,
        vararg friends: FriendEntity,
    ) {
        require(friends.all { it.userId == userId }) { "Friends must belong to user $userId" }
        database.friendDao().insert(friends.toList())
    }

    fun storedFriends(userId: Long): List<FriendEntity> = database.friendDao().getUserFriends(userId).blockingFirst()

    fun givenValidCache() = givenCacheTimestamp(time.now)

    fun givenCacheTimestamp(millis: Long) {
        sharedPreferences.edit { putLong(ContactCacheImpl.LAST_MODIFIED_DATE, millis) }
    }

    fun givenExpiredCache() {
        sharedPreferences.edit { remove(ContactCacheImpl.LAST_MODIFIED_DATE) }
    }

    fun cacheTimestamp(): Long? =
        sharedPreferences.takeIf { it.contains(ContactCacheImpl.LAST_MODIFIED_DATE) }
            ?.getLong(ContactCacheImpl.LAST_MODIFIED_DATE, 0)

    /** Waits until [count] HTTP calls have completely finished, including the Rx chain after them. */
    fun awaitCallsFinished(count: Int = 1) {
        finishedCalls.awaitCount(count)
    }

    fun close() {
        database.close()
    }
}
