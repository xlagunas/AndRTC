package cat.xlagunas.andrtc.data.net.webrtc;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;

/**
 * Created by xlagunas on 25/7/16.
 */

public class WebRTCManagerImpl implements WebRTCManager {
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";

    private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";
    private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";


    private static final String TAG = WebRTCManagerImpl.class.getSimpleName();

    private final Transport transport;
    private final EglBase eglBase;
    private final Executor executor;
    private final WebRTCAudioManager audioManager;

    private PeerConnectionFactory factory;
    private MediaConstraints videoConstraints;
    private MediaConstraints peerConnectionConstraints;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
    private AudioSource localAudioSource;
    private VideoSource localVideoSource;
    private MediaStream localMediaStream;

    private List<PeerConnection.IceServer> iceServerList;
    private Map<String, PeerData> peerConnectionMap;

    private ConferenceListener listener;

    @Inject
    Context context;

    @Inject
    public WebRTCManagerImpl(WebRTCAudioManager audioManager, EglBase eglBase, Executor executor) {
        this.transport = transport;
        this.eglBase = eglBase;
        this.executor = executor;
        this.audioManager = audioManager;
        peerConnectionMap = new HashMap<>();
    }

    @Override
    public void setConferenceListener(ConferenceListener listener) {
        this.listener = listener;
    }

    @Override
    public void initLocalSource(SurfaceViewRenderer localRenderer, VideoCapturer capturer) {
        executor.execute(() -> {
            localRenderer.init(eglBase.getEglBaseContext(), null);
            createVideoConstraints(1280, 720, 30);
            localVideoSource = factory.createVideoSource(capturer, videoConstraints);
            localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);
            localVideoTrack.setEnabled(true);
            localVideoTrack.addRenderer(new VideoRenderer(localRenderer));

            localAudioSource = factory.createAudioSource(new MediaConstraints());
            localAudioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, localAudioSource);


            listener.onLocalVideoGenerated(localVideoSource);
        });

    }

    @Override
    public void addRendererForUser(String userId, SurfaceViewRenderer renderer) {
        executor.execute(() -> {
            PeerData data = peerConnectionMap.get(userId);
            if (data != null){
                data.setRemoteRenderer(renderer);
                data.getRemoteRenderer().init(eglBase.getEglBaseContext(), null);
                data.getRemoteVideoTrack().addRenderer(new VideoRenderer(renderer));
            }
        });
    }

    @Override
    public void init() {
        executor.execute(() -> {
            PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true);
            transport.setWebRTCCallbacks(WebRTCManagerImpl.this);
            createPeerConstraints();

            factory = new PeerConnectionFactory(new PeerConnectionFactory.Options());
            factory.setVideoHwAccelerationOptions(eglBase.getEglBaseContext(), eglBase.getEglBaseContext());
            initIceServers();

            transport.init();
        });

    }

    private void initIceServers() {
        iceServerList = new ArrayList<>();
        PeerConnection.IceServer iceServer = new PeerConnection.IceServer("turn:xlagunas.cat", "Hercules", "X4v1");
        iceServerList.add(iceServer);
    }

    public void stop() {
        executor.execute(() -> {
            transport.disconnect();
            Iterator<PeerData> iterator = peerConnectionMap.values().iterator();
            while (iterator.hasNext()) {
                PeerData peerData = iterator.next();
                peerData.getRemoteRenderer().release();
                peerData.setRemoteVideoTrack(null);
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


    public void createVideoConstraints(int width, int height, int frameRate) {
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

    public void createPeerConstraints() {
        peerConnectionConstraints = new MediaConstraints();
        peerConnectionConstraints.optional.add(
                new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));
    }

    @Override
    public MediaConstraints getCallConstraints(boolean offerToReceiveVideo, boolean offerToReceiveAudio) {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", offerToReceiveAudio ? "true" : "false"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", offerToReceiveVideo ? "true" : "false"));
        return sdpMediaConstraints;
    }

    public PeerConnection createPeerConnection(PeerConnection.Observer observer) {

        PeerConnection peerConnection = factory.createPeerConnection(iceServerList, peerConnectionConstraints, observer);
        if (localMediaStream == null) {
            localMediaStream = factory.createLocalMediaStream("ARDAMS");
            localMediaStream.addTrack(localVideoTrack);
            localMediaStream.addTrack(localAudioTrack);
        }

        peerConnection.addStream(localMediaStream);

        return peerConnection;
    }




    public PeerConnection.Observer generatePeerObserverPerUserId(String userId, PeerConnection peerConnection) {

        return new PeerObserver(userId){

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);

                    if (peerConnection != null) {

                        if (mediaStream.audioTracks.size() > 1 || mediaStream.videoTracks.size() > 1) {
                            return;
                        }

                        if (mediaStream.videoTracks.size() == 1) {

                            VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                            remoteVideoTrack.setEnabled(true);
                            peerData.setRemoteVideoTrack(remoteVideoTrack);

                            //DEFER the attachment of the media stream to when the user has provided a surface
                            listener.onNewMediaStreamReceived(userId);

                        }
                    } else {
                        Log.wtf(TAG, "Call onAddStream on a null peerconnection");
                    }
            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                super.onRemoveStream(mediaStream);
                executor.execute(() ->
                        peerConnectionMap.get(userId).setRemoteVideoTrack(null));
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                super.onIceCandidate(iceCandidate);
                executor.execute(() -> {
                    transport.sendIceCandidate(userId, iceCandidate);
                });
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                super.onIceConnectionChange(iceConnectionState);
                if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED){
                    Log.d(TAG, "Connection with user "+userId+" has been finished, removing it");
                    PeerData data = peerConnectionMap.get(userId);
                    data.getRemoteVideoTrack().dispose();
                    data.setQueuedRemoteCandidates(null);
                    data.getPeerConnection().close();
                    Log.d(TAG, "proceeding to delete stream");
                    listener.onConnectionClosed(data.getRemoteRenderer());
                    peerConnectionMap.remove(userId);
                } else if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED){
                    audioManager.init();
                }
            }
        };

    }

    public interface ConferenceListener {
        void onLocalVideoGenerated(VideoSource videoSource);
        void onNewMediaStreamReceived(String userId);
        void onConnectionClosed(SurfaceViewRenderer renderer);
    }
}
