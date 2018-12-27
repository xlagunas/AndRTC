package cat.xlagunas.conference.di

import android.app.Application
import cat.xlagunas.conference.data.ConferenceRepositoryImp
import cat.xlagunas.conference.data.utils.CoroutinesStreamAdapterFactory
import cat.xlagunas.conference.data.WebRTCEventHandler
import cat.xlagunas.conference.data.dto.mapper.ConferenceeMapper
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.utils.UserSessionIdentifier
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
import okhttp3.internal.Util
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.EglBase
import org.webrtc.HardwareVideoEncoderFactory
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import javax.inject.Singleton

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
        lifecycle: Lifecycle,
        userSessionIdentifier: UserSessionIdentifier
    ): Scarlet {
        return Scarlet.Builder()
            .webSocketFactory(okHttpClient.newWebSocketFactory("ws://192.168.0.158:8080/$roomId/${userSessionIdentifier.getUserId()}"))
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

    @Provides
    fun provideEglContext(): EglBase.Context {
        return EglBase.create().eglBaseContext
    }

    @Provides
    fun providePeerConnectionFactory(
        context: EglBase.Context,
        initOptions: PeerConnectionFactory.InitializationOptions
    ): PeerConnectionFactory {
        PeerConnectionFactory.initialize(initOptions)

        return PeerConnectionFactory.builder()
            .setVideoEncoderFactory(HardwareVideoEncoderFactory(context, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(context))
            .createPeerConnectionFactory()
    }

    @Provides
    fun providePeerConnectionFactoryInitialisingOptions(application: Application): PeerConnectionFactory.InitializationOptions {
        return PeerConnectionFactory.InitializationOptions.builder(application).createInitializationOptions()
    }

    @Provides
    fun provideRTCConfiguration(): PeerConnection.RTCConfiguration {
        val iceServerList = Util.immutableList(PeerConnection.IceServer("turn:xlagunas.cat", "Hercules", "X4v1"))
        return PeerConnection.RTCConfiguration(iceServerList)
    }

    @Provides
    @Singleton
    fun provideWebRTCEventHandler() = WebRTCEventHandler()

    @Provides
    @Singleton
    fun provideConferenceeMapper() : ConferenceeMapper{
        return ConferenceeMapper()
    }
}