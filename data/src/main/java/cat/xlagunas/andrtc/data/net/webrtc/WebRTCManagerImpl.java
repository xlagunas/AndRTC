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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

/**
 * Created by xlagunas on 25/7/16.
 */

public class WebRTCManagerImpl implements WebRTCManager, WebRTCCallbacks {
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final String AUDIO_TRACK_ID = "ARDAMSa0";

    private static final String VIDEO_CODEC_VP8 = "VP8";

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
    private AudioTrack localAudioTrack;
    private AudioSource localAudioSource;
    private VideoSource localVideoSource;
    private MediaStream localMediaStream;

    private List<PeerConnection.IceServer> iceServerList;
    private Map<String, PeerData> peerConnectionMap;
    private Executor executor;

    private ConferenceListener listener;

    @Inject
    Context context;

    @Inject
    public WebRTCManagerImpl(Transport transport, EglBase eglBase, Executor executor) {
        this.transport = transport;
        this.eglBase = eglBase;
        this.executor = executor;
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

                //At this point remoteVideoTrack should never be null...
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
        iceServerList = new ArrayList<>(ICE_SERVERS.length);
        for (String iceServerUrl : ICE_SERVERS) {
            iceServerList.add(new PeerConnection.IceServer(iceServerUrl));
        }
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

    public MediaConstraints getCallConstraints(boolean video, boolean audio) {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", audio ? "true" : "false"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", video ? "true" : "false"));
        return sdpMediaConstraints;
    }

    @Override
    public void createNewPeerConnection(String userId, boolean createAsInitiator) {

        PeerConnection peerConnection = factory.createPeerConnection(iceServerList, peerConnectionConstraints, generatePeerObserverPerUserId(userId));
        if (localMediaStream == null) {
            localMediaStream = factory.createLocalMediaStream("ARDAMS");
            localMediaStream.addTrack(localVideoTrack);
            localMediaStream.addTrack(localAudioTrack);
        }

        peerConnection.addStream(localMediaStream);

        final SdpObserver sdpObserver = new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "oncreateSuccess: " + sessionDescription.description);
                executor.execute(() -> peerConnection.setLocalDescription(peerConnectionMap.get(userId).getObserver(), sessionDescription));
            }

            @Override
            public void onSetSuccess() {
                executor.execute(() -> {
                    Log.d(TAG, "oncreateSuccess");
                    if (createAsInitiator) {
                        if (peerConnection.getRemoteDescription() == null) {
                            transport.sendOffer(userId, peerConnection.getLocalDescription());
                        } else {
                            Log.d(TAG, "Draining the ice candidates being a initiator");
                            drainCandidates(userId);
                        }
                    } else {
                        if (peerConnection.getLocalDescription() != null) {
                            transport.sendAnswer(userId, peerConnection.getLocalDescription());
                            Log.d(TAG, "Draining the ice candidates being a answerer");
                            drainCandidates(userId);
                        }
                    }
                });
            }

            @Override
            public void onCreateFailure(String s) {
                Log.d(TAG, "onCreateFailure " + s);
            }

            @Override
            public void onSetFailure(String s) {
                Log.d(TAG, "onSetFailure " + s);
            }
        };

        peerConnectionMap.put(userId, new PeerData(peerConnection, sdpObserver));

        if (createAsInitiator) {
            peerConnection.createOffer(sdpObserver, getCallConstraints(true, true));
        }

    }

    @Override
    public void onAnswerReceived(String senderId, JSONObject receivedAnswer) {
        PeerData peerData = peerConnectionMap.get(senderId);

        PeerConnection peerConnection = peerData.getPeerConnection();
        try {
            SessionDescription sdpDescription = new SessionDescription(SessionDescription.Type.ANSWER, receivedAnswer.getString("sdp"));
            peerConnection.setRemoteDescription(peerData.getObserver(), sdpDescription);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsinganswer: ", e);
        }
    }

    @Override
    public void onOfferReceived(String senderId, JSONObject receivedOffer) {
        PeerData peerData = peerConnectionMap.get(senderId);

        PeerConnection peerConnection = peerData.getPeerConnection();
        try {
            //if offer comes from web (chrome) offer comes inside sdp, otherwise comes inside description
            String offerField = receivedOffer.isNull("sdp") ? "description" : "sdp";
            SessionDescription sdpDescription = new SessionDescription
                    (SessionDescription.Type.OFFER, receivedOffer.getString(offerField));

            peerConnection.setRemoteDescription(peerData.getObserver(), sdpDescription);
            peerConnection.createAnswer(peerData.getObserver(), getCallConstraints(true, true));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing: ", e);
        }


    }

    @Override
    public void onIceCandidateReceived(String senderId, JSONObject receivedIceCandidate) {
        executor.execute(() -> {
            try {
                IceCandidate iceCandidate = (new IceCandidate(receivedIceCandidate.getString("sdpMid"), receivedIceCandidate.getInt("sdpMLineIndex"), receivedIceCandidate.getString("candidate")));
                LinkedList<IceCandidate> queuedRemoteCandidates = peerConnectionMap.get(senderId).getQueuedRemoteCandidates();
                if (queuedRemoteCandidates == null) {
                    queuedRemoteCandidates = new LinkedList<>();
                }
                queuedRemoteCandidates.add(iceCandidate);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing IceCandidate");
            }
        });

    }

    private void drainCandidates(String userId) {
        executor.execute(() -> {
            PeerData peerData = peerConnectionMap.get(userId);
            PeerConnection peerConnection = peerData.getPeerConnection();
            LinkedList<IceCandidate> queuedRemoteCandidates = peerData.getQueuedRemoteCandidates();
            if (queuedRemoteCandidates != null) {
                Log.d(TAG, "Add " + queuedRemoteCandidates.size() + " remote candidates");
                for (IceCandidate candidate : queuedRemoteCandidates) {
                    peerConnection.addIceCandidate(candidate);
                }
                peerData.setQueuedRemoteCandidates(null);
            }
        });
    }

    public interface ConferenceListener {
        void onLocalVideoGenerated(VideoSource videoSource);
        void onNewMediaStreamReceived(String userId);
    }

    private PeerConnection.Observer generatePeerObserverPerUserId(String userId) {

        return new PeerObserver(userId){

            @Override
            public void onAddStream(MediaStream mediaStream) {
                super.onAddStream(mediaStream);
                executor.execute(() -> {
                    PeerData peerData = peerConnectionMap.get(userId);
                    PeerConnection peerConnection = peerData.getPeerConnection();

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
                });
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
        };

    }
}
