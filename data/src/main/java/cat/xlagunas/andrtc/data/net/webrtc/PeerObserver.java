package cat.xlagunas.andrtc.data.net.webrtc;

import android.util.Log;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

import timber.log.Timber;

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
        Timber.d( "onSignalingChange for user: " + userId + " :" + signalingState.toString());
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Timber.d( "onIceConnectionChange for user: " + userId + " :" + iceConnectionState.toString());
    }

    @Override
    public void onIceConnectionReceivingChange(boolean b) {
        Timber.d( "onIceConnectionReceivingChange for user: " + userId + " :" + b);

    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Timber.d( "onIceGatheringChange for user: " + userId + " :" + iceGatheringState.toString());

    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        Timber.d( "onIceCandidate for user: " + userId);

    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Timber.d( "onIceCandidatesRemoved for user: " + userId + " : total:" + iceCandidates.length);

    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        Timber.d( "onAddStream for user: " + userId + " :" + mediaStream.label());

    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Timber.d( "onRemoveStream for user: " + userId + " :" + mediaStream.label());
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Timber.d( "onDataChannel for user: " + userId + ": " + dataChannel.label());
    }

    @Override
    public void onRenegotiationNeeded() {
        Timber.d( "onRenegotiationNeeded!");
    }
}
