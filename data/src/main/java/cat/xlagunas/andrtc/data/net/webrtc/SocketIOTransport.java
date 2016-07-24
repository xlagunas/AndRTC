package cat.xlagunas.andrtc.data.net.webrtc;

import java.net.URISyntaxException;

import javax.inject.Inject;

import cat.xlagunas.andrtc.data.net.webrtc.messages.JoinRoomMsg;
import cat.xlagunas.andrtc.data.net.webrtc.messages.LoginMessage;
import io.socket.client.IO;
import io.socket.client.Socket;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 22/07/16.
 */
public class SocketIOTransport implements Transport {
    private static final String TAG = SocketIOTransport.class.getSimpleName();

    private final User user;

    private Socket socket;

    @Inject
    public SocketIOTransport(User user){
        this.user = user;
    }

    @Override
    public void init() {
        try {
            socket = IO.socket("http://127.0.0.1:3000");
            socket.connect();
            socket.emit("login", new LoginMessage(user.getUsername(), user.getPassword()));
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
}
