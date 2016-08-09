package cat.xlagunas.andrtc.data.net.webrtc;

import org.json.JSONObject;

/**
 * Created by xlagunas on 25/7/16.
 */
public interface WebRTCCallbacks {
    void createNewPeerConnection(String userId, boolean createAsInitiator);
    void onAnswerReceived(String senderId, JSONObject receivedAnswer);
    void onOfferReceived(String senderId, JSONObject receivedOffer);
    void onIceCandidateReceived(String senderId, JSONObject receivedIceCandidate);
}
