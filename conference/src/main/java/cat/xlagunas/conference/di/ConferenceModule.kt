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
import di.Feature
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.audio.AudioDeviceModule
import org.webrtc.audio.JavaAudioDeviceModule

@Module
class ConferenceModule {

    @Provides
    @Feature
    fun provideRepository(repository: ConferenceRepositoryImp): ConferenceRepository {
        return repository
    }

    @Provides
    @Feature
    fun provideSocketLifecycleFactory(activity: ConferenceActivity, roomId: String): SocketIoLifecycle.Factory {
        return SocketIoLifecycle.Factory(activity, roomId)
    }

    @Provides
    @Feature
    fun provideSocketIo(socketIoLifecycleFactory: SocketIoLifecycle.Factory, okHttpClient: OkHttpClient): Socket {

        IO.setDefaultOkHttpCallFactory(okHttpClient)

        return IO.socket("https://wss.viv.cat")
        // return IO.socket("http://192.168.0.155:3000")
                .also {
                    socketIoLifecycleFactory.create(it).init()
                }
    }

    @Provides
    @Feature
    fun provideEglContext(): EglBase.Context {
        return EglBase.create().eglBaseContext
    }

    @Provides
    @Feature
    fun providePeerConnectionFactory(
        context: EglBase.Context,
        initOptions: PeerConnectionFactory.InitializationOptions,
        audioDeviceModule: AudioDeviceModule
    ): PeerConnectionFactory {
        PeerConnectionFactory.initialize(initOptions)

        PeerConnectionFactory.Options()

        return PeerConnectionFactory.builder()
                .setOptions(PeerConnectionFactory.Options())
                .setVideoEncoderFactory(DefaultVideoEncoderFactory(context, true, true))
                .setVideoDecoderFactory(DefaultVideoDecoderFactory(context))
                .setAudioDeviceModule(audioDeviceModule)
                .createPeerConnectionFactory()
    }

    @Provides
    fun provideAudioDeviceModule(application: Application): AudioDeviceModule {
        return JavaAudioDeviceModule.builder(application)
//                .setSamplesReadyCallback(saveRecordedAudioToFile)
//                .setUseHardwareAcousticEchoCanceler(!peerConnectionParameters.disableBuiltInAEC)
//                .setUseHardwareNoiseSuppressor(!peerConnectionParameters.disableBuiltInNS)
//                .setAudioRecordErrorCallback(audioRecordErrorCallback)
//                .setAudioTrackErrorCallback(audioTrackErrorCallback)
                .createAudioDeviceModule()
    }

    @Provides
    fun providePeerConnectionFactoryInitialisingOptions(application: Application): PeerConnectionFactory.InitializationOptions {
        return PeerConnectionFactory.InitializationOptions.builder(application)
                .setEnableInternalTracer(true)
                .createInitializationOptions()
    }

    @Provides
    fun provideRTCConfiguration(): PeerConnection.RTCConfiguration {

//        val server = listOf(
//                PeerConnection.IceServer("turn:xlagunas.cat", "Hercules", "X4v1"))

        return PeerConnection.RTCConfiguration(provideGoogleTurnServers())
                .also {
                    it.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
                    it.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
                    it.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
                    it.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
                    it.keyType = PeerConnection.KeyType.ECDSA
                    it.enableDtlsSrtp = true
                    it.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
                }
    }

    private fun provideGoogleTurnServers(): List<PeerConnection.IceServer> {
        return listOf(PeerConnection.IceServer.builder("stun:[2a00:1450:400c:c06::7f]:19302").setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE).createIceServer(),
                PeerConnection.IceServer.builder("stun:66.102.1.127:19302").setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE).createIceServer(),
                PeerConnection.IceServer.builder("turn:74.125.140.127:19305?transport=udp").setUsername("CJ73qeMFEgYNfwosX/EYzc/s6OMTIICjBQ").setPassword("SBi08lceLMsFWYdAQsT0XDnW4i4=").setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE).createIceServer(),
                PeerConnection.IceServer.builder("turn:[2a00:1450:400c:c08::7f]:19305?transport=udp").setUsername("CJ73qeMFEgYNfwosX/EYzc/s6OMTIICjBQ").setPassword("SBi08lceLMsFWYdAQsT0XDnW4i4=").setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE).createIceServer(),
                PeerConnection.IceServer.builder("turn:74.125.140.127:19305?transport=tcp").setUsername("CJ73qeMFEgYNfwosX/EYzc/s6OMTIICjBQ").setPassword("SBi08lceLMsFWYdAQsT0XDnW4i4==").setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE).createIceServer(),
                PeerConnection.IceServer.builder("turn:[2a00:1450:400c:c08::7f]:19305?transport=tcp").setUsername("CJ73qeMFEgYNfwosX/EYzc/s6OMTIICjBQ").setPassword("SBi08lceLMsFWYdAQsT0XDnW4i4==").setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE).createIceServer()
        )
    }

    @Provides
    @Feature
    fun provideWebRTCEventHandler() = WebRTCEventHandler()

    @Provides
    @Feature
    fun provideConferenceeMapper(): ConferenceeMapper {
        return ConferenceeMapper()
    }

    @Provides
    @Feature
    // TODO PROBABLY THIS NEEDS TO GO UP IN THE APP GRAPH
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Feature
    fun provideUserSession(socket: Socket): UserSessionIdentifier {
        return SocketSessionIdentifier(socket)
    }

    @Provides
    @Feature
    fun provideMessageDtoMapper(gson: Gson, userSessionIdentifier: UserSessionIdentifier): MessageDtoMapper {
        return MessageDtoMapper(gson, userSessionIdentifier)
    }

    @Provides
    @Feature
    fun providePeerConnectionDataSource(
        peerConnectionFactory: PeerConnectionFactory,
        webRTCEventHandler: WebRTCEventHandler,
        rtcConfiguration: PeerConnection.RTCConfiguration
    ): PeerConnectionDataSource {
        return PeerConnectionDataSource(peerConnectionFactory, webRTCEventHandler, rtcConfiguration)
    }
}