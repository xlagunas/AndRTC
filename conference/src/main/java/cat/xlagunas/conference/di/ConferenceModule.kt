package cat.xlagunas.conference.di

import android.app.Application
import cat.xlagunas.conference.data.ConferenceRepositoryImp
import cat.xlagunas.conference.data.SocketIoLifecycle
import cat.xlagunas.conference.data.WebRTCEventHandler
import cat.xlagunas.conference.data.dto.mapper.ConferenceeMapper
import cat.xlagunas.conference.data.dto.mapper.MessageDtoMapper
import cat.xlagunas.conference.data.utils.SocketSessionIdentifier
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.PeerConnectionDataSource
import cat.xlagunas.conference.domain.utils.UserSessionIdentifier
import cat.xlagunas.conference.ui.ConferenceActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import io.socket.client.IO
import io.socket.client.Socket
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
    @Singleton
    fun provideSocketLifecycleFactory(activity: ConferenceActivity, roomId: String): SocketIoLifecycle.Factory {
        return SocketIoLifecycle.Factory(activity, roomId)
    }

    @Provides
    @Singleton
    fun provideSocketIo(socketIoLifecycleFactory: SocketIoLifecycle.Factory, okHttpClient: OkHttpClient): Socket {

        IO.setDefaultOkHttpCallFactory(okHttpClient)

        return IO.socket("http://192.168.0.155:8080")
                .also {
                    socketIoLifecycleFactory.create(it).init()
                }
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
                .also { it.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN }
    }

    @Provides
    @Singleton
    fun provideWebRTCEventHandler() = WebRTCEventHandler()

    @Provides
    @Singleton
    fun provideConferenceeMapper(): ConferenceeMapper {
        return ConferenceeMapper()
    }

    @Provides
    @Singleton
    //TODO PROBABLY THIS NEEDS TO GO UP IN THE APP GRAPH
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideUserSession(socket: Socket): UserSessionIdentifier {
        return SocketSessionIdentifier(socket)
    }


    @Provides
    @Singleton
    fun provideMessageDtoMapper(gson: Gson, userSessionIdentifier: UserSessionIdentifier): MessageDtoMapper {
        return MessageDtoMapper(gson, userSessionIdentifier)
    }

    @Provides
    @Singleton
    fun providePeerConnectionDataSource(
            peerConnectionFactory: PeerConnectionFactory,
            webRTCEventHandler: WebRTCEventHandler,
            rtcConfiguration: PeerConnection.RTCConfiguration
    ): PeerConnectionDataSource {
        return PeerConnectionDataSource(peerConnectionFactory, webRTCEventHandler, rtcConfiguration)
    }
}