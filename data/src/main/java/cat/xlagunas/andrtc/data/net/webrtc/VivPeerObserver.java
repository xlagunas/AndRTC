package cat.xlagunas.andrtc.data.net.webrtc;

import android.util.Log;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.VideoTrack;

public class VivPeerObserver extends PeerObserver {

    private final static String TAG = VivPeerObserver.class.getSimpleName();

    private final PeerData peerData;
    private PeerConnection peerConnection;
    private final WebRTCManagerImpl.ConferenceListener listener;
    private boolean isGatheringComplete;

    public VivPeerObserver(PeerData peerData, WebRTCManagerImpl.ConferenceListener listener, String userId) {
        super(userId);
        this.peerData = peerData;
        this.listener = listener;
    }


    @Override
    public void onAddStream(MediaStream mediaStream) {
        super.onAddStream(mediaStream);

        if (peerData != null && peerConnection != null) {

            if (mediaStream.audioTracks.size() > 1 || mediaStream.videoTracks.size() > 1) {
                return;
            }

            if (mediaStream.videoTracks.size() == 1) {

                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                remoteVideoTrack.setEnabled(true);
                peerData.setRemoteVideoTrack(remoteVideoTrack);

                //DEFFER the attachment of the media stream to when the user has provided a surface
                listener.onNewMediaStreamReceived(userId);

            }
        } else {
            Log.wtf(TAG, "Call onAddStream on a null peerconnection");
        }
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        super.onRemoveStream(mediaStream);
        peerData.setRemoteVideoTrack(null);
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        super.onIceCandidate(iceCandidate);
        //We are on complete status, we don't need more candidates
        if (!isGatheringComplete) {
            listener.onIceCandidateGenerated(userId, iceCandidate);
        }
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        super.onIceConnectionChange(iceConnectionState);
        if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
            Log.d(TAG, "Connection with user " + userId + " has been finished, removing it");
            peerData.getRemoteVideoTrack().dispose();
            peerData.setQueuedRemoteCandidates(null);
            peerData.getPeerConnection().close();
            Log.d(TAG, "proceeding to delete stream");
            listener.onDisconnected(userId);
        } else if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
            listener.onConnected(userId);
        }
    }

    public void setPeerConnection(PeerConnection peerConnection) {
        this.peerConnection = peerConnection;
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        super.onIceGatheringChange(iceGatheringState);
        if (iceGatheringState == PeerConnection.IceGatheringState.COMPLETE) {
            this.isGatheringComplete = true;
            listener.drainCandidates(userId);
        } else {
            this.isGatheringComplete = false;
        }
    }
}
