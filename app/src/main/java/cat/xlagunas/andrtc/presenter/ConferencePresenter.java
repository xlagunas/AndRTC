package cat.xlagunas.andrtc.presenter;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.net.webrtc.PeerData;
import cat.xlagunas.andrtc.data.net.webrtc.Transport;
import cat.xlagunas.andrtc.data.net.webrtc.VivPeerObserver;
import cat.xlagunas.andrtc.data.net.webrtc.VivSdpObserver;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCAudioManager;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCCallbacks;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManager;
import cat.xlagunas.andrtc.view.ConferenceDataView;

/**
 * Created by xlagunas on 3/8/16.
 */
public class ConferencePresenter implements Presenter, WebRTCCallbacks, VivSdpObserver.SdpEvents {
    private static final String TAG = ConferencePresenter.class.getSimpleName();

    private final Transport transport;
    private final WebRTCManager webRTCManager;
    private final WebRTCAudioManager audioManager;

    private Map<String, PeerData> peerConnectionMap;

    private ConferenceDataView view;

    @Inject
    public ConferencePresenter(Transport transport, WebRTCManager webRTCManager, WebRTCAudioManager audioManager) {
        this.transport = transport;
        this.webRTCManager = webRTCManager;
        this.audioManager = audioManager;
        //Typically maximum users are going to be <5
        this.peerConnectionMap = new HashMap<>(5);
    }

    public void setView(ConferenceDataView view) {
        this.view = view;
    }

    @Override
    public void resume() {
        webRTCManager.init();
        view.startCameraStream();
        transport.setWebRTCCallbacks(this);
        transport.init();

    }

    @Override
    public void pause() {
        Log.d(TAG, "Destroying all instances in presenter");
        Iterator<PeerData> iterator = peerConnectionMap.values().iterator();
        while (iterator.hasNext()) {
            PeerData peerData = iterator.next();
            peerData.getRemoteRenderer().release();
            peerData.setRemoteVideoTrack(null);
            peerData.getPeerConnection().dispose();
        }
        transport.disconnect();
        webRTCManager.stop();
    }

    @Override
    public void destroy() {

    }


    //Transport callbacks reacting to signaling events

    @Override
    public void createNewPeerConnection(String userId, boolean createAsInitiator) {
        PeerData peerData = new PeerData();
        VivPeerObserver peerObserver = new VivPeerObserver(peerData, view, userId);
        VivSdpObserver sdpObserver = new VivSdpObserver(userId, this, createAsInitiator);

        PeerConnection connection = webRTCManager.createPeerConnection(peerObserver);

        peerData.setPeerConnection(connection);
        peerData.setObserver(sdpObserver);

        // SDPObserver and PeerObserver are mainly callbacks that need a pointer to the peerConnection
        //but at same time, to create a peer connection we need a SdpObserver so there's no way we can
        // pass this as a paremeter in the constructor..
        sdpObserver.setCurrentConnection(connection);
        peerObserver.setPeerConnection(connection);

        peerConnectionMap.put(userId, peerData);

        if (createAsInitiator) {
            connection.createOffer(sdpObserver, webRTCManager.getCallConstraints(true, true));
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
            Log.e(TAG, "Error parsing answer received from user: " + senderId, e);
        }
    }

    @Override
    public void onOfferReceived(String senderId, JSONObject receivedOffer) {
        PeerData peerData = peerConnectionMap.get(senderId);

        PeerConnection peerConnection = peerData.getPeerConnection();
        try {
            SessionDescription sdpDescription = new SessionDescription(SessionDescription.Type.OFFER, receivedOffer.getString("sdp"));

            peerConnection.setRemoteDescription(peerData.getObserver(), sdpDescription);
            peerConnection.createAnswer(peerData.getObserver(), webRTCManager.getCallConstraints(true, true));
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing offer received from user: " + senderId, e);
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


    public void startRenderingVideo(String userId, SurfaceViewRenderer renderer) {
        PeerData peerData = peerConnectionMap.get(userId);
        webRTCManager.assignRendererToSurface(peerData.getRemoteVideoTrack(), renderer);
        peerData.setRemoteRenderer(renderer);
        view.updateLayout();
    }

    public void sendIceCandidate(String userId, IceCandidate iceCandidate) {
        transport.sendIceCandidate(userId, iceCandidate);
    }

    public void cleanConnection(String userId) {
        SurfaceViewRenderer remoteRenderer = peerConnectionMap.get(userId).getRemoteRenderer();
        view.removeRenderer(remoteRenderer);
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
        webRTCManager.drainRemoteCandidates(peerData.getPeerConnection(), peerData.getQueuedRemoteCandidates());
        peerData.setQueuedRemoteCandidates(null);
    }

    public void initLocalSource(SurfaceViewRenderer localRenderer, VideoCapturer videoSource) {
        webRTCManager.setConferenceListener(view);
        webRTCManager.initLocalSource(localRenderer, videoSource);
    }
}
