package cat.xlagunas.andrtc.data.net.webrtc;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;

import java.util.LinkedList;

/**
 * Created by xlagunas on 4/8/16.
 */
public class PeerData {
    private PeerConnection peerConnection;
    private SdpObserver observer;
    private LinkedList<IceCandidate> queuedRemoteCandidates;

    public PeerData(PeerConnection peerConnection, SdpObserver observer) {
        this.peerConnection = peerConnection;
        this.observer = observer;
        this.queuedRemoteCandidates = new LinkedList<>();
    }

    public PeerConnection getPeerConnection() {
        return peerConnection;
    }

    public SdpObserver getObserver() {
        return observer;
    }


    public LinkedList<IceCandidate> getQueuedRemoteCandidates() {
        return queuedRemoteCandidates;
    }

    public void setQueuedRemoteCandidates(LinkedList<IceCandidate> queuedRemoteCandidates) {
        this.queuedRemoteCandidates = queuedRemoteCandidates;
    }
}
