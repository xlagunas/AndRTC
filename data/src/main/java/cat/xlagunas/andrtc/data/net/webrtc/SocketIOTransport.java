package cat.xlagunas.andrtc.data.net.webrtc;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.UserEntity;
import cat.xlagunas.andrtc.data.net.webrtc.messages.JoinRoomMsg;
import cat.xlagunas.andrtc.data.net.webrtc.messages.LoginMessage;
import cat.xlagunas.andrtc.data.net.webrtc.messages.UserDetailsMessage;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
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
            opts.reconnection = true;
            opts.secure = false;

            socket = IO.socket("http://192.168.1.133:3000", opts);
            socket.on(Socket.EVENT_CONNECT, args -> {
                socket.emit("login", gson.toJson(new LoginMessage(user.getUsername(), user.getPassword())));
                Log.d(TAG, "Emitted login event");
            });
            socket.on("login", args -> {
                socket.emit("call:register", gson.toJson(new JoinRoomMsg(roomId)));
            });
            socket.on("call:addUser", new Emitter.Listener() {
                @Override
                public void call(Object... userObject) {
                    Log.d(TAG, userObject.toString());
//                    socket.emit('call:userDetails', gson.toJsonTree(new UserDetailsMessage())
                }
            });
            socket.on(Socket.EVENT_DISCONNECT, args -> {
                Log.d(TAG, "received disconnect event");
            });

            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
//       executor.execute(new Runnable() {
//           @Override
//           public void run() {
//               connect();
//           }
//       });
        connect();
    }

    @Override
    public void connectToRoom(String roomId) {
        try {
            Socket socket = IO.socket("http://127.0.0.1:3000");
            socket.connect();
            socket.emit(Transport.JOIN_ROOM, new JoinRoomMsg(roomId));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        socket.disconnect();
    }

    @Override
    public void sendOffer() {

    }

    @Override
    public void sendAnswer() {

    }

    @Override
    public void sendIceCandidate() {

    }

    @Override
    public void setWebRTCCallbacks(WebRTCCallbacks callbacks) {
        this.callbacks = callbacks;
    }

}
