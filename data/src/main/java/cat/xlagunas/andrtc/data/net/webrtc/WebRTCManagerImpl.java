package cat.xlagunas.andrtc.data.net.webrtc;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.net.webrtc.messages.WebRTCMessage;
import rx.Observable;
import rx.Subscriber;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 25/7/16.
 */

public class WebRTCManagerImpl implements WebRTCManager{
    private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";
    
    private static final String[] ICE_SERVERS = {
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302",
            "stun:stun3.l.google.com:19302",
            "stun:stun4.l.google.com:19302"
    };

    final Transport transport;

    private MediaConstraints videoConstraints;
    private MediaConstraints sdpMediaConstraints;

    @Inject
    public WebRTCManagerImpl(Transport transport){
        this.transport = transport;
    }

//    public void start(WebRTCCallbacks callbacks){
//        transport.setWebRTCCallbacks(callbacks);
//        transport.init();
//    }


    @Override
    public void init() {
        transport.init();
    }

    public void stop(){
        transport.setWebRTCCallbacks(null);
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

    public void createCallConstraints(boolean video, boolean audio){
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", audio ? "true" : "false"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", video ? "true" : "false"));
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
}
