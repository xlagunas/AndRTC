package cat.xlagunas.conference.di

import android.app.Application
import cat.xlagunas.conference.data.ConferenceRepositoryImp
import cat.xlagunas.conference.data.CoroutinesStreamAdapterFactory
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.WsMessagingApi
import cat.xlagunas.conference.ui.ConferenceActivity
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient

@Module
class ConferenceModule {

    @Provides
    fun provideRepository(repository: ConferenceRepositoryImp): ConferenceRepository {
        return repository
    }

    @Provides
    fun provideScarletInstance(
        okHttpClient: OkHttpClient,
        roomId: String,
        lifecycle: Lifecycle
    ): Scarlet {
        return Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory("ws://192.168.0.158:8080/$roomId/testUser"))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(CoroutinesStreamAdapterFactory())
            .lifecycle(lifecycle)
            .build()
    }

    @Provides
    fun provideWebsocketService(scarlet: Scarlet): WsMessagingApi {
        return scarlet.create(WsMessagingApi::class.java)
    }

    @Provides
    fun provideLifecycleRegistry(activity: ConferenceActivity, application: Application): Lifecycle {
        return AndroidLifecycle.ofLifecycleOwnerForeground(application, activity)
    }
}