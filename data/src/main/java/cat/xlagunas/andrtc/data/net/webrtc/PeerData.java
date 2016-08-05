package cat.xlagunas.andrtc.data.net.webrtc;

import org.webrtc.PeerConnection;
import org.webrtc.SdpObserver;

/**
 * Created by xlagunas on 4/8/16.
 */
public class PeerData {
    private PeerConnection peerConnection;
    private SdpObserver observer;

    public PeerData(PeerConnection peerConnection, SdpObserver observer) {
        this.peerConnection = peerConnection;
        this.observer = observer;
    }

    public PeerConnection getPeerConnection() {
        return peerConnection;
    }

    public SdpObserver getObserver() {
        return observer;
    }
}
