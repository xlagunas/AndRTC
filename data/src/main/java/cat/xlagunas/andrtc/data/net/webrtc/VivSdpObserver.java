package cat.xlagunas.andrtc.data.net.webrtc;

import android.util.Log;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

/**
 * Created by xlagunas on 11/8/16.
 */
public class VivSdpObserver implements SdpObserver {

    private static final String TAG = VivSdpObserver.class.getSimpleName();
    private final String userId;
    private final boolean shouldGenerateOffer;
    private final SdpEvents sdpEvents;
    private PeerConnection peerConnection;

    public VivSdpObserver(String userId, SdpEvents events, boolean shouldGenerateOffer) {
        this.userId = userId;
        this.shouldGenerateOffer = shouldGenerateOffer;
        this.sdpEvents = events;
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        Log.d(TAG, "Successfully created LocalDescription");
        peerConnection.setLocalDescription(this, sessionDescription);
    }

    @Override
    public void onSetSuccess() {
        Log.d(TAG, "oncreateSuccess");
        if (shouldGenerateOffer) {
            if (peerConnection.getRemoteDescription() == null) {
                sdpEvents.onOfferGenerated(userId, peerConnection.getLocalDescription());
            } else {
                Log.d(TAG, "Draining the ice candidates being a initiator");
//                sdpEvents.onFinishedGatheringIceCandidates(userId);
            }
        } else {
            if (peerConnection.getLocalDescription() != null) {
                sdpEvents.onAnswerGenerated(userId, peerConnection.getLocalDescription());
                Log.d(TAG, "Draining the ice candidates being a answerer");
//                sdpEvents.onFinishedGatheringIceCandidates(userId);
            }
        }
    }

    @Override
    public void onCreateFailure(String s) {
        Log.e(TAG, "Error creating offer/answer: " +s);
    }

    @Override
    public void onSetFailure(String s) {
       Log.e(TAG, "Error setting offer/answer: "+s);
    }

    public void setCurrentConnection(PeerConnection currentConnection) {
        this.peerConnection = currentConnection;
    }

    public interface SdpEvents {
        void onOfferGenerated(String userId, SessionDescription localSessionDescription);
        void onAnswerGenerated(String userId, SessionDescription localSessionDescription);
        void onFinishedGatheringIceCandidates(String userId);
    }
}