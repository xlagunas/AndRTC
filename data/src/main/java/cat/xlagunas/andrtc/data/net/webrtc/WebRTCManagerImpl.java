package cat.xlagunas.andrtc.data.net.webrtc;

import android.content.Context;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    private final EglBase eglBase;
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

    private ConferenceListener listener;

    @Inject
    Context context;

    @Inject
    public WebRTCManagerImpl(WebRTCAudioManager audioManager, EglBase eglBase) {
        this.eglBase = eglBase;
        this.audioManager = audioManager;
        peerConnectionConstraints = new MediaConstraints();
        peerConnectionConstraints.optional.add(
                new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));
    }

    @Override
    public void setConferenceListener(ConferenceListener listener) {
        this.listener = listener;
    }

    @Override
    public void initLocalSource(SurfaceViewRenderer localRenderer, VideoCapturer capturer) {
        localRenderer.init(eglBase.getEglBaseContext(), null);
        createVideoConstraints(1280, 720, 30);
        localVideoSource = factory.createVideoSource(capturer, videoConstraints);
        localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);
        localVideoTrack.setEnabled(true);
        localVideoTrack.addRenderer(new VideoRenderer(localRenderer));

        localAudioSource = factory.createAudioSource(new MediaConstraints());
        localAudioTrack = factory.createAudioTrack(AUDIO_TRACK_ID, localAudioSource);

        listener.onLocalVideoGenerated(localVideoSource);

    }

    @Override
    public void assignRendererToSurface(VideoTrack videoTrack, SurfaceViewRenderer renderer) {
        if (renderer != null && videoTrack != null){
            renderer.init(eglBase.getEglBaseContext(), null);
            videoTrack.addRenderer(new VideoRenderer(renderer));
        } else {
            Log.e(TAG, "Can't assign renderer to videoTrack, either one is null");
        }
    }

    @Override
    public void init() {
        PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true);
        factory = new PeerConnectionFactory(new PeerConnectionFactory.Options());
        factory.setVideoHwAccelerationOptions(eglBase.getEglBaseContext(), eglBase.getEglBaseContext());
        initIceServers();
    }

    private void initIceServers() {
        iceServerList = new ArrayList<>();
        PeerConnection.IceServer iceServer = new PeerConnection.IceServer("turn:xlagunas.cat", "Hercules", "X4v1");
        iceServerList.add(iceServer);
    }

    public void stop() {
        if (localVideoSource != null) {
            localVideoSource = null;
        }
        if (factory != null) {
            factory.dispose();
        }
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

    @Override
    public MediaConstraints getCallConstraints(boolean offerToReceiveVideo, boolean offerToReceiveAudio) {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", offerToReceiveAudio ? "true" : "false"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", offerToReceiveVideo ? "true" : "false"));
        return sdpMediaConstraints;
    }

    @Override
    public void drainRemoteCandidates(PeerConnection peerConnection, LinkedList<IceCandidate> queuedRemoteCandidates) {
        if (queuedRemoteCandidates != null) {
            Log.d(TAG, "Add " + queuedRemoteCandidates.size() + " remote candidates");
            for (IceCandidate candidate : queuedRemoteCandidates) {
                peerConnection.addIceCandidate(candidate);
            }
        }
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


    public interface ConferenceListener {
        void onLocalVideoGenerated(VideoSource videoSource);

        void onNewMediaStreamReceived(String userId);

        void onIceCandidateGenerated(String userId, IceCandidate iceCandidate);

        void onConnected(String userId);

        void onDisconnected(String userId);

        void drainCandidates(String userId);
    }
}
