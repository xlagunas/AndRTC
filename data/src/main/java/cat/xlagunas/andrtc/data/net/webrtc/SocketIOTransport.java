package cat.xlagunas.andrtc.data.net.webrtc;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.net.webrtc.messages.JoinRoomMsg;
import cat.xlagunas.andrtc.data.net.webrtc.messages.LoginMessage;
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

    private Socket socket;
    private Gson gson;

    private WebRTCCallbacks callbacks;

    @Inject
    public SocketIOTransport(User user) {
        this.user = user;
    }

    @Override
    public void init() {
        try {
            gson = new Gson();

            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            opts.secure = false;

            socket = IO.socket("http://192.168.1.134:3000", opts);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socket.emit("login", gson.toJsonTree(new LoginMessage(user.getUsername(), user.getPassword())));
                }

            });
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("error");
                }
            });
            socket.on("login", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    callbacks.onCreateAnswer(null);
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

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
