package xlagunas.cat.data;

import org.junit.Before;
import org.junit.Test;

import cat.xlagunas.andrtc.data.net.webrtc.SocketIOTransport;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 22/07/16.
 */
public class SocketIOTest {

    private User user;

    @Before
    public void init(){
        user = new User();
        user.setUsername("xlagunas");
        user.setPassword("123456");
    }

    @Test
    public void checkConnection(){
        SocketIOTransport socket = new SocketIOTransport(user);
        socket.init();
    }
}
