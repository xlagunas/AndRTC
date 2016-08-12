package cat.xlagunas.andrtc.data.net.webrtc;

import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

/**
 * Created by xlagunas on 9/8/16.
 */
public class PeerObserver implements PeerConnection.Observer {

    private final static String TAG = PeerObserver.class.getSimpleName();
    protected final String userId;

    public String getUserId() {
        return userId;
    }

    public PeerObserver(String userId) {
        this.userId = userId;
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(TAG, "onSignalingChange for user: " + userId + " :" + signalingState.toString());
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.d(TAG, "onIceConnectionChange for user: " + userId + " :" + iceConnectionState.toString());
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Log.d(TAG, "onIceConnectionReceivingChange for user: " + userId + " :" + b);

    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.d(TAG, "onIceGatheringChange for user: " + userId + " :" + iceGatheringState.toString());

    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Log.d(TAG, "onIceCandidate for user: " + userId);

    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.d(TAG, "onIceCandidatesRemoved for user: " + userId + " : total:" + iceCandidates.length);

    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        Log.d(TAG, "onAddStream for user: " + userId + " :" + mediaStream.label());

    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.d(TAG, "onRemoveStream for user: " + userId + " :" + mediaStream.label());
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.d(TAG, "onDataChannel for user: " + userId + ": " + dataChannel.label());
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.d(TAG, "onRenegotiationNeeded!");
    }
}
