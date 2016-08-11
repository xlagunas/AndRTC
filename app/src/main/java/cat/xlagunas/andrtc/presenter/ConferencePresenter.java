package cat.xlagunas.andrtc.presenter;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import cat.xlagunas.andrtc.data.net.webrtc.PeerData;
import cat.xlagunas.andrtc.data.net.webrtc.PeerObserver;
import cat.xlagunas.andrtc.data.net.webrtc.Transport;
import cat.xlagunas.andrtc.data.net.webrtc.VivSdpObserver;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCAudioManager;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCCallbacks;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManager;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManagerImpl;

/**
 * Created by xlagunas on 3/8/16.
 */
public class ConferencePresenter implements Presenter, WebRTCCallbacks, WebRTCManagerImpl.ConferenceListener, VivSdpObserver.SdpEvents {
    private static final String TAG = ConferencePresenter.class.getSimpleName();

    private final Transport transport;
    private final WebRTCManager manager;
    private final WebRTCAudioManager audioManager;

    private Map<String, PeerData> peerConnectionMap;

    public ConferencePresenter(Transport transport, WebRTCManager manager, WebRTCAudioManager audioManager){
        this.transport = transport;
        this.manager = manager;
        this.audioManager = audioManager;
        //Typically maximum users are going to be <5
        this.peerConnectionMap = new HashMap<>(5);
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }

    //Transport callbacks reacting to signaling events

    @Override
    public void createNewPeerConnection(String userId, boolean createAsInitiator) {
        PeerObserver observer = new PeerObserver(userId);
        PeerConnection connection = manager.createPeerConnection(observer);
        VivSdpObserver sdpObserver = new VivSdpObserver(userId, connection, this, createAsInitiator);
        PeerData peerData = new PeerData(connection, sdpObserver);

        peerConnectionMap.put(userId, peerData);

        if (createAsInitiator) {
            connection.createOffer(sdpObserver, manager.getCallConstraints(true, true));
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
            Log.e(TAG, "Error parsing answer received from user: "+senderId, e);
        }
    }

    @Override
    public void onOfferReceived(String senderId, JSONObject receivedOffer) {
        PeerData peerData = peerConnectionMap.get(senderId);

        PeerConnection peerConnection = peerData.getPeerConnection();
        try {
            SessionDescription sdpDescription = new SessionDescription(SessionDescription.Type.OFFER, receivedOffer.getString("sdp"));

            peerConnection.setRemoteDescription(peerData.getObserver(), sdpDescription);
            peerConnection.createAnswer(peerData.getObserver(), manager.getCallConstraints(true, true));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing offer received from user: "+senderId, e);
        }

    }

    @Override
    public void onIceCandidateReceived(String senderId, JSONObject receivedIceCandidate) {
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
    }

//    ConferenceCallbacks from the Manager
    @Override
    public void onLocalVideoGenerated(VideoSource videoSource) {

    }

    @Override
    public void onNewMediaStreamReceived(String userId) {

    }

    @Override
    public void onConnectionClosed(SurfaceViewRenderer renderer) {

    }

    //SdpEvents

    @Override
    public void onOfferGenerated(String userId, SessionDescription localSessionDescription) {
        transport.sendOffer(userId, localSessionDescription);
    }

    @Override
    public void onAnswerGenerated(String userId, SessionDescription localSessionDescription) {
        transport.sendAnswer(userId, localSessionDescription);
    }

    @Override
    public void onFinishedGatheringIceCandidates(String userId) {
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
    }
}
