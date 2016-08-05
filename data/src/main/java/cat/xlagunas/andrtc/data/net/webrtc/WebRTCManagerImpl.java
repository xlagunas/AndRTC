package cat.xlagunas.andrtc.data.net.webrtc;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;

/**
 * Created by xlagunas on 25/7/16.
 */

public class WebRTCManagerImpl implements WebRTCManager, WebRTCCallbacks {
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";

    private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";


    private static final String[] ICE_SERVERS = {
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302",
            "stun:stun3.l.google.com:19302",
            "stun:stun4.l.google.com:19302"
    };
    private static final String TAG = WebRTCManagerImpl.class.getSimpleName();

    final Transport transport;

    private PeerConnectionFactory factory;
    private EglBase eglBase;
    private MediaConstraints videoConstraints;
    private MediaConstraints peerConnectionConstraints;
    private VideoTrack localVideoTrack;
    private VideoSource localVideoSource;

    private List<PeerConnection.IceServer> iceServerList;
    private Map<String, PeerData> peerConnectionMap;
    private Executor executor;

    private ConferenceListener listener;

    @Inject
    Context context;

    @Inject
    public WebRTCManagerImpl(Transport transport, EglBase eglBase, Executor executor){
        this.transport = transport;
        this.eglBase = eglBase;
        this.executor = executor;
        peerConnectionMap = new HashMap<>();
    }

    @Override
    public void setConferenceListener(ConferenceListener listener) {
        this.listener = listener;
    }

    public PeerConnectionFactory getPeerConnectionFactory(){
        return factory;
    }

    @Override
    public void initLocalSource(SurfaceViewRenderer localRenderer, VideoCapturer capturer) {
        executor.execute(()-> {
            localRenderer.init(eglBase.getEglBaseContext(), null);
            createVideoConstraints(1280, 720, 30);
            localVideoSource = factory.createVideoSource(capturer, videoConstraints);

            localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);
            localVideoTrack.setEnabled(true);
            localVideoTrack.addRenderer(new VideoRenderer(localRenderer));

            listener.onLocalVideoGenerated(localVideoSource);
        });

    }


    @Override
    public void init() {
        executor.execute(() -> {
            PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true);
            transport.setWebRTCCallbacks(WebRTCManagerImpl.this);
            createPeerConstraints();

            factory = new PeerConnectionFactory(new PeerConnectionFactory.Options());
            factory.setVideoHwAccelerationOptions(eglBase.getEglBaseContext(), null);
            initIceServers();

            transport.init();
        });

    }

    private void initIceServers() {
        iceServerList = new ArrayList<>(ICE_SERVERS.length);
        for (String iceServerUrl: ICE_SERVERS) {
            iceServerList.add(new PeerConnection.IceServer(iceServerUrl));
        }
    }

    public void stop(){
        executor.execute(()-> {
            transport.disconnect();
            Iterator<PeerData> iterator = peerConnectionMap.values().iterator();
            while (iterator.hasNext()) {
                PeerData peerData = iterator.next();
                peerData.getPeerConnection().dispose();
            }
            if (localVideoSource != null) {
                localVideoSource = null;
            }
            eglBase.release();
            factory.dispose();
            transport.setWebRTCCallbacks(null);
        });
    }



    public void createVideoConstraints(int width, int height, int frameRate){
        videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_WIDTH_CONSTRAINT, Integer.toString(1280)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_WIDTH_CONSTRAINT, Integer.toString(1280)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(720)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(720)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_FPS_CONSTRAINT, Integer.toString(30)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_FPS_CONSTRAINT, Integer.toString(30)));
    }

    public void createPeerConstraints(){
        peerConnectionConstraints = new MediaConstraints();
        peerConnectionConstraints.optional.add(
                new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));
    }

    public MediaConstraints getCallConstraints(boolean video, boolean audio){
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", audio ? "true" : "false"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", video ? "true" : "false"));
        return sdpMediaConstraints;
    }
    
    private PeerConnection.RTCConfiguration getRTCConfiguration(List<PeerConnection.IceServer> iceServers) {

        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(iceServers);
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;

        return rtcConfig;
    }

    @Override
    public void createNewPeerConnection(String userId, boolean createAsInitiator) {
        PeerConnection peerConnection = factory.createPeerConnection(iceServerList, peerConnectionConstraints, new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange");

            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(TAG, "onIceConnectionReceivingChange");

            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange");

            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d(TAG, "onIceCandidate");

            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved");

            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream");

            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream");

            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel");

            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded");

            }
        });
        final SdpObserver sdpObserver = new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "oncreateSuccess");
                executor.execute(() -> peerConnection.setLocalDescription(peerConnectionMap.get(userId).getObserver(), sessionDescription));
            }

            @Override
            public void onSetSuccess() {
                Log.d(TAG, "oncreateSuccess");
                if (createAsInitiator){
                    transport.sendOffer(userId, peerConnection.getLocalDescription());
                } else {
                    transport.sendAnswer(userId, peerConnection.getLocalDescription());
                }

            }

            @Override
            public void onCreateFailure(String s) {
                Log.d(TAG, "oncreateSuccess");

            }

            @Override
            public void onSetFailure(String s) {
                Log.d(TAG, "oncreateSuccess");

            }
        };

        peerConnectionMap.put(userId, new PeerData(peerConnection,sdpObserver));

    }

    @Override
    public void onAnswerReceived(String senderId, JSONObject receivedAnswer) {

    }

    @Override
    public void onOfferReceived(String senderId, JSONObject receivedOffer) {
        PeerData peerData = peerConnectionMap.get(senderId);

        PeerConnection peerConnection = peerData.getPeerConnection();
        try  {
            peerConnection.setRemoteDescription(peerData.getObserver(), new SessionDescription(SessionDescription.Type.OFFER, receivedOffer.getString("sdp")));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onIceCandidate(String senderId, JSONObject receivedIceCandidate) {

    }

    public interface ConferenceListener {
        void onLocalVideoGenerated(VideoSource videouSource);
    }
}
