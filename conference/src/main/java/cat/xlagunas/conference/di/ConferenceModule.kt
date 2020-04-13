package cat.xlagunas.conference.di

import android.app.Application
import cat.xlagunas.conference.data.ConferenceRepositoryImp
import cat.xlagunas.conference.domain.ConferenceRepository
import cat.xlagunas.conference.domain.PeerConnectionDataSource
import cat.xlagunas.conference.ui.ConferenceActivity
import cat.xlagunas.ws_messaging.LifecycleAwareWebSocketProvider
import cat.xlagunas.ws_messaging.SignalingProtocol
import cat.xlagunas.ws_messaging.WebSocketController
import cat.xlagunas.ws_messaging.WebSocketEmitterProvider
import cat.xlagunas.ws_messaging.WsSignalingProtocol
import cat.xlagunas.ws_messaging.data.SessionAdapter
import cat.xlagunas.ws_messaging.model.MessageMapper
import cat.xlagunas.ws_messaging.model.Session
import cat.xlagunas.ws_messaging.model.SessionMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import di.Feature
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
    fun provideSignaling(websocketController: WebSocketController): SignalingProtocol {
        return WsSignalingProtocol(websocketController)
    }

    @Provides
    @Feature
    fun provideWebSocketController(
        messageMapper: MessageMapper,
        sessionMapper: SessionMapper,
        webSocketEmitterProvider: WebSocketEmitterProvider
    ): WebSocketController {
        return WebSocketController(messageMapper, sessionMapper, webSocketEmitterProvider)
    }

    @Provides
    @Feature
    fun provideWebSocketEmitterProvider(
        activity: ConferenceActivity,
        okHttpClient: OkHttpClient
    ): WebSocketEmitterProvider {
        return LifecycleAwareWebSocketProvider(activity, okHttpClient)
    }

    @Provides
    @Feature
    fun provideMessageMapper(gson: Gson): MessageMapper {
        return MessageMapper(gson)
    }

    @Provides
    @Feature
    fun provideSessionMapper(gson: Gson): SessionMapper {
        return SessionMapper(gson)
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
                .setUseHardwareAcousticEchoCanceler(true)
                .setUseHardwareNoiseSuppressor(true)
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

        return PeerConnection.RTCConfiguration(provideGoogleTurnServers())
            .also {
                it.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
                it.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
                it.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
                it.continualGatheringPolicy =
                    PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
                it.keyType = PeerConnection.KeyType.ECDSA
                it.enableDtlsSrtp = true
                it.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            }
    }

    private fun provideGoogleTurnServers(): List<PeerConnection.IceServer> {
        return listOf(
            PeerConnection.IceServer.builder("stun:[2a00:1450:400c:c06::7f]:19302")
                .setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE)
                .createIceServer(),
            PeerConnection.IceServer.builder("stun:66.102.1.127:19302")
                .setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE)
                .createIceServer(),
            PeerConnection.IceServer.builder("turn:74.125.140.127:19305?transport=udp")
                .setUsername("CJ73qeMFEgYNfwosX/EYzc/s6OMTIICjBQ")
                .setPassword("SBi08lceLMsFWYdAQsT0XDnW4i4=")
                .setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE)
                .createIceServer(),
            PeerConnection.IceServer.builder("turn:[2a00:1450:400c:c08::7f]:19305?transport=udp")
                .setUsername("CJ73qeMFEgYNfwosX/EYzc/s6OMTIICjBQ")
                .setPassword("SBi08lceLMsFWYdAQsT0XDnW4i4=")
                .setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE)
                .createIceServer(),
            PeerConnection.IceServer.builder("turn:74.125.140.127:19305?transport=tcp")
                .setUsername("CJ73qeMFEgYNfwosX/EYzc/s6OMTIICjBQ")
                .setPassword("SBi08lceLMsFWYdAQsT0XDnW4i4==")
                .setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE)
                .createIceServer(),
            PeerConnection.IceServer.builder("turn:[2a00:1450:400c:c08::7f]:19305?transport=tcp")
                .setUsername("CJ73qeMFEgYNfwosX/EYzc/s6OMTIICjBQ")
                .setPassword("SBi08lceLMsFWYdAQsT0XDnW4i4==")
                .setTlsCertPolicy(PeerConnection.TlsCertPolicy.TLS_CERT_POLICY_SECURE)
                .createIceServer()
        )
    }

    @Provides
    @Feature
    // TODO PROBABLY THIS NEEDS TO GO UP IN THE APP GRAPH
    fun provideGson(): Gson {
        return GsonBuilder().registerTypeAdapter(Session::class.java, SessionAdapter()).create()
    }

    @Provides
    @Feature
    fun providePeerConnectionDataSource(
        peerConnectionFactory: PeerConnectionFactory,
        rtcConfiguration: PeerConnection.RTCConfiguration
    ): PeerConnectionDataSource {
        return PeerConnectionDataSource(peerConnectionFactory, rtcConfiguration)
    }
}