package cat.xlagunas.andrtc.data.net.webrtc;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.net.webrtc.messages.AnswerMessage;
import cat.xlagunas.andrtc.data.net.webrtc.messages.IceCandidateMessage;
import cat.xlagunas.andrtc.data.net.webrtc.messages.JoinRoomMsg;
import cat.xlagunas.andrtc.data.net.webrtc.messages.LoginMessage;
import cat.xlagunas.andrtc.data.net.webrtc.messages.OfferMessage;
import io.socket.client.IO;
import io.socket.client.Socket;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 22/07/16.
 */
public class SocketIOTransport implements Transport {
    private static final String TAG = SocketIOTransport.class.getSimpleName();

    private final User user;
    private final Executor executor;
    private final String roomId;

    private Socket socket;
    private Gson gson;

    private WebRTCCallbacks callbacks;

    @Inject
    public SocketIOTransport(User user, Executor executor, String roomId) {
        this.user = user;
        this.executor = executor;
        this.roomId = roomId;
    }

    private void connect(){
        try {
            gson = new Gson();

            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;
            opts.secure = false;

            socket = IO.socket("http://192.168.1.133:3000", opts);
            socket.on(Socket.EVENT_CONNECT, args -> {
                socket.emit("login", gson.toJson(new LoginMessage(user.getUsername(), user.getPassword())));
                Log.d(TAG, "Emitted login event");
            });
            socket.on("login", args -> {
                socket.emit("call:register", gson.toJson(new JoinRoomMsg(roomId)));
            });
            socket.on("call:addUser", userObject -> {
                Log.d(TAG, "call:addUser");
                JSONObject user = (JSONObject) userObject[0];

                try {
                    String userId = user.getString("_id");
                    socket.on(userId+":answer", callArgs -> {
                        Log.d(TAG, "Received an answer from user"+userId);
                        JSONObject receivedAnswer = (JSONObject) callArgs[0];
                        callbacks.onAnswerReceived(userId, receivedAnswer);
                    });
                    callbacks.createNewPeerConnection(userId, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            socket.on("call:userDetails", args -> {
                Log.d(TAG, "call:userDetails");
                JSONObject user1 = (JSONObject) args[0];
                try {
                    String userId = user1.getString("_id");
                    socket.on(userId+":offer", callArgs -> {
                        Log.d(TAG, "Received an offer from user"+userId);
                        JSONObject receivedOffer = (JSONObject) callArgs[0];
                        callbacks.onOfferReceived(userId, receivedOffer);
                    });
                    callbacks.createNewPeerConnection(userId, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });

            socket.connect();

            socket.on(Socket.EVENT_DISCONNECT, args -> {
            Log.d(TAG, "received disconnect event");
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
       executor.execute(() -> connect());
    }


    @Override
    public void disconnect() {
        socket.disconnect();
    }

    @Override
    public void sendOffer(String userId, SessionDescription localDescription) {
        Log.d(TAG, "Sending offer message");
        OfferMessage message = new OfferMessage(userId, roomId, localDescription.toString());
        socket.emit("webrtc:offer", gson.toJson(message));
    }

    @Override
    public void sendAnswer(String userId, SessionDescription localDescription) {
        Log.d(TAG, "Sending answer message");
        AnswerMessage message = new AnswerMessage(userId, roomId, localDescription);
        socket.emit("webrtc:answer", gson.toJson(message));
    }

    @Override
    public void sendIceCandidate(String userId, IceCandidate iceCandidate) {
        Log.d(TAG, "Sending Ice candidate");
        IceCandidateMessage message = new IceCandidateMessage(userId, roomId, iceCandidate);
        socket.emit("webrtc:iceCandidate", gson.toJson(message));
    }

    @Override
    public void setWebRTCCallbacks(WebRTCCallbacks callbacks) {
        this.callbacks = callbacks;
    }

}
