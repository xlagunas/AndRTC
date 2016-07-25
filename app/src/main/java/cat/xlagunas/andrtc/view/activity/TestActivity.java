package cat.xlagunas.andrtc.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.net.URISyntaxException;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.data.net.webrtc.messages.LoginMessage;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 25/7/16.
 */
public class TestActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final User user = new User();
        user.setPassword("123456");
        user.setUsername("xlagunas");

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = true;
            opts.secure = false;

            final Socket socket = IO.socket("http://192.168.1.134:3000", opts);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socket.emit("login", new LoginMessage(user.getUsername(), user.getPassword()));
                    socket.disconnect();
                }

            });
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("error");
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
