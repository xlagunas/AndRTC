package cat.xlagunas.viv.push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import cat.xlagunas.call.CallMessageProcessor
import cat.xlagunas.contact.data.ContactCacheImpl
import cat.xlagunas.contact.data.ContactRepositoryImpl
import cat.xlagunas.contact.data.ContactsApi
import cat.xlagunas.contact.data.FriendConverter
import cat.xlagunas.contact.data.LocalContactDataSource
import cat.xlagunas.contact.data.RemoteContactDataSource
import cat.xlagunas.contact.domain.TimeProvider
import cat.xlagunas.contact.ui.ContactsMessageProcessor
import cat.xlagunas.core.network.HttpClientFactory
import cat.xlagunas.core.persistence.AuthDataStore
import cat.xlagunas.core.persistence.db.UserEntity
import cat.xlagunas.core.persistence.db.VivDatabase
import cat.xlagunas.core.push.MessageProcessor
import cat.xlagunas.core.push.MessageType
import cat.xlagunas.push.MessageConverter
import cat.xlagunas.push.PushMessageRouter
import cat.xlagunas.push.PushTokenApi
import cat.xlagunas.push.PushTokenPresenter
import cat.xlagunas.push.PushTokenRepositoryImpl
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.FakePushTokenProvider
import cat.xlagunas.test.utils.awaitFirst
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.user.auth.AuthHeaderInterceptor
import cat.xlagunas.user.auth.AuthPreferenceDataStore
import com.google.gson.Gson
import org.robolectric.Shadows.shadowOf
import javax.inject.Provider

/**
 * Push routing and token rotation wired by hand with its production classes and the real processors. Only
 * HTTP ([FakeBackend]) and Firebase are fakes; notifications go to Robolectric's shadow.
 */
class PushCoreHarness(
    backend: FakeBackend,
    rule: CoreTestRule,
) {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java)

    val pushTokenProvider = FakePushTokenProvider()

    // Same preferences file as ApplicationModule.provideSharedPreference.
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    val dataStore: AuthDataStore = AuthPreferenceDataStore(sharedPreferences)

    // Trampoline schedulers run some Room queries on the calling thread.
    val database: VivDatabase =
        Room.inMemoryDatabaseBuilder(context, VivDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    private val retrofit =
        HttpClientFactory.retrofit(
            backend.url,
            HttpClientFactory.okHttpClient(listOf(AuthHeaderInterceptor(dataStore))),
            Gson(),
        )

    private val contactsMessageProcessor =
        ContactsMessageProcessor(
            ContactRepositoryImpl(
                LocalContactDataSource(database.friendDao(), dataStore, FriendConverter()),
                RemoteContactDataSource(retrofit.create(ContactsApi::class.java), FriendConverter()),
                ContactCacheImpl(sharedPreferences, FixedTimeProvider),
                rule.rxSchedulers,
            ),
        )

    private val callMessageProcessor: CallMessageProcessor

    init {
        notificationManager.createNotificationChannel(
            NotificationChannel(CALL_CHANNEL_ID, "Calls", NotificationManager.IMPORTANCE_HIGH),
        )
        callMessageProcessor =
            CallMessageProcessor(
                context,
                NotificationManagerCompat.from(context),
                CALL_CHANNEL_ID,
                Gson(),
                rule.testScope,
            )
    }

    // Same keys as PushMessageProcessorsModule.
    private val messageProcessors: Map<MessageType, Provider<MessageProcessor>> =
        mapOf(
            MessageType.REQUEST_FRIENDSHIP to Provider { contactsMessageProcessor },
            MessageType.ACCEPT_FRIENDSHIP to Provider { contactsMessageProcessor },
            MessageType.REJECT_FRIENDSHIP to Provider { contactsMessageProcessor },
            MessageType.CREATE_CALL to Provider { callMessageProcessor },
        )

    val router = PushMessageRouter(MessageConverter(), messageProcessors)

    val pushTokenPresenter =
        PushTokenPresenter(
            PushTokenRepositoryImpl(pushTokenProvider, retrofit.create(PushTokenApi::class.java), rule.rxSchedulers),
            dataStore,
        )

    fun notifications(): List<Notification> = shadowOf(notificationManager).allNotifications

    fun givenLoggedInUser(id: Long = 3) {
        database.userDao().insert(UserEntity(id, "anna", "Anna", "Puig", "anna@example.com", null))
        dataStore.insertAuthToken("token-123")
        dataStore.updateCurrentUserId(id)
    }

    fun friendsOf(userId: Long = 3): List<String> = database.friendDao().getUserFriends(userId).awaitFirst().map { it.username }

    fun close() {
        database.close()
    }

    private object FixedTimeProvider : TimeProvider {
        override fun getTimeMillis(): Long = 1_700_000_000_000L
    }

    private companion object {
        const val CALL_CHANNEL_ID = "CALL_CHANNEL_ID"
    }
}
