package cat.xlagunas.andrtc.data.net.webrtc;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

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

    private static final String VIDEO_CODEC_VP8 = "VP8";
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String VIDEO_CODEC_H264 = "H264";
    private static final String AUDIO_CODEC_OPUS = "opus";
    private static final String AUDIO_CODEC_ISAC = "ISAC";
    private static final String VIDEO_CODEC_PARAM_START_BITRATE =  "x-google-start-bitrate";
    private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";



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
    private MediaStream localMediaStream;

    private LinkedList<IceCandidate> queuedRemoteCandidates;


    //TODO THIS NEEDS REFACTOR!
    private VideoTrack remoteVideoTrack;
    private SurfaceViewRenderer remoteRenderer;


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

    public void setRemoteRendererSource(SurfaceViewRenderer remoteRenderer){
        this.remoteRenderer = remoteRenderer;
        remoteRenderer.init(eglBase.getEglBaseContext(), null);
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
                Log.d(TAG, "onSignalingChange" +new Gson().toJson(signalingState));
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange"+new Gson().toJson(iceConnectionState));

            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(TAG, "onIceConnectionReceivingChange"+ new Gson().toJson(b));

            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange" +new Gson().toJson(iceGatheringState));

            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d(TAG, "onIceCandidate"+ new Gson().toJson(iceCandidate));
                executor.execute(()-> {
                    transport.sendIceCandidate(userId, iceCandidate);
                });
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved");

            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream");
                executor.execute(() -> {
                    PeerConnection peerConnection = peerConnectionMap.get(userId).getPeerConnection();

                    if (peerConnection != null) {

                        if (mediaStream.videoTracks.size() == 1) {
                            remoteVideoTrack = mediaStream.videoTracks.get(0);
                            remoteVideoTrack.setEnabled(true);
                            remoteVideoTrack.addRenderer(new VideoRenderer(remoteRenderer));
                        }
                    } else {
                        Log.wtf(TAG, "Call onAddStream on a null peerconnection");
                    }
                });


            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream");

            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel" +new Gson().toJson(dataChannel));

            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded");

            }
        });
        if (localMediaStream == null){
            localMediaStream = factory.createLocalMediaStream("ARDAMS");
            localMediaStream.addTrack(localVideoTrack);
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
                executor.execute(()-> {
                    Log.d(TAG, "oncreateSuccess");
                    if (createAsInitiator){
                        transport.sendOffer(userId, peerConnection.getLocalDescription());
                    } else {
                        if (peerConnection.getLocalDescription() != null) {
                            transport.sendAnswer(userId, peerConnection.getLocalDescription());
                        }
                    }
                });
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

        if (createAsInitiator){
            peerConnection.createOffer(sdpObserver, getCallConstraints(true, false));
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
            Log.e(TAG, "Error parsinganswer: ",e);
        }
    }

    @Override
    public void onOfferReceived(String senderId, JSONObject receivedOffer) {
        PeerData peerData = peerConnectionMap.get(senderId);

        PeerConnection peerConnection = peerData.getPeerConnection();
        try  {
            //if offer comes from web (chrome) offer comes inside sdp, otherwise comes inside description
            String offerField = receivedOffer.isNull("sdp") ? "description" : "sdp";
            SessionDescription sdpDescription = new SessionDescription
                    (SessionDescription.Type.OFFER, receivedOffer.getString(offerField));

            String description = preferCodec(sdpDescription.description, VIDEO_CODEC_VP8, false);
            SessionDescription sdpRemote = new SessionDescription(
                    sdpDescription.type, description);

            peerConnection.setRemoteDescription(peerData.getObserver(), sdpRemote);
            peerConnection.createAnswer(peerData.getObserver(), getCallConstraints(true, false));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing: ",e);
        }


    }

    @Override
    public void onIceCandidate(String senderId, JSONObject receivedIceCandidate) {
//        peerConnectionMap.get(senderId).getPeerConnection().addIceCandidate(new IceCandidate())
        PeerConnection peerConnection = peerConnectionMap.get(senderId).getPeerConnection();
        try {
            peerConnection.addIceCandidate(new IceCandidate(receivedIceCandidate.getString("sdpMid"), receivedIceCandidate.getInt("sdpMLineIndex"), receivedIceCandidate.getString("candidate")));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing IceCandidate");
        }

    }

    public interface ConferenceListener {
        void onLocalVideoGenerated(VideoSource videouSource);
    }

    private static String setStartBitrate(String codec, boolean isVideoCodec,
                                          String sdpDescription, int bitrateKbps) {
        String[] lines = sdpDescription.split("\r\n");
        int rtpmapLineIndex = -1;
        boolean sdpFormatUpdated = false;
        String codecRtpMap = null;
        // Search for codec rtpmap in format
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                rtpmapLineIndex = i;
                break;
            }
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec + " codec");
            return sdpDescription;
        }
        Log.d(TAG, "Found " +  codec + " rtpmap " + codecRtpMap
                + " at " + lines[rtpmapLineIndex]);

        // Check if a=fmtp string already exist in remote SDP for this codec and
        // update it with new bitrate parameter.
        regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
        codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                Log.d(TAG, "Found " +  codec + " " + lines[i]);
                if (isVideoCodec) {
                    lines[i] += "; " + VIDEO_CODEC_PARAM_START_BITRATE
                            + "=" + bitrateKbps;
                } else {
                    lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE
                            + "=" + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Update remote SDP line: " + lines[i]);
                sdpFormatUpdated = true;
                break;
            }
        }

        StringBuilder newSdpDescription = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            newSdpDescription.append(lines[i]).append("\r\n");
            // Append new a=fmtp line if no such line exist for a codec.
            if (!sdpFormatUpdated && i == rtpmapLineIndex) {
                String bitrateSet;
                if (isVideoCodec) {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " "
                            + VIDEO_CODEC_PARAM_START_BITRATE + "=" + bitrateKbps;
                } else {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " "
                            + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Add remote SDP line: " + bitrateSet);
                newSdpDescription.append(bitrateSet).append("\r\n");
            }

        }
        return newSdpDescription.toString();
    }

    private static String preferCodec(
            String sdpDescription, String codec, boolean isAudio) {
        String[] lines = sdpDescription.split("\r\n");
        int mLineIndex = -1;
        String codecRtpMap = null;
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        String mediaDescription = "m=video ";
        if (isAudio) {
            mediaDescription = "m=audio ";
        }
        for (int i = 0; (i < lines.length)
                && (mLineIndex == -1 || codecRtpMap == null); i++) {
            if (lines[i].startsWith(mediaDescription)) {
                mLineIndex = i;
                continue;
            }
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
            }
        }
        if (mLineIndex == -1) {
            Log.w(TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
            return sdpDescription;
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec);
            return sdpDescription;
        }
        Log.d(TAG, "Found " +  codec + " rtpmap " + codecRtpMap + ", prefer at "
                + lines[mLineIndex]);
        String[] origMLineParts = lines[mLineIndex].split(" ");
        if (origMLineParts.length > 3) {
            StringBuilder newMLine = new StringBuilder();
            int origPartIndex = 0;
            // Format is: m=<media> <port> <proto> <fmt> ...
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(codecRtpMap);
            for (; origPartIndex < origMLineParts.length; origPartIndex++) {
                if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
                    newMLine.append(" ").append(origMLineParts[origPartIndex]);
                }
            }
            lines[mLineIndex] = newMLine.toString();
            Log.d(TAG, "Change media description: " + lines[mLineIndex]);
        } else {
            Log.e(TAG, "Wrong SDP media description format: " + lines[mLineIndex]);
        }
        StringBuilder newSdpDescription = new StringBuilder();
        for (String line : lines) {
            newSdpDescription.append(line).append("\r\n");
        }
        return newSdpDescription.toString();
    }
}
