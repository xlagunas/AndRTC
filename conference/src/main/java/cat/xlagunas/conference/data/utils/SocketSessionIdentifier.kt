package cat.xlagunas.conference.data.utils

import cat.xlagunas.conference.domain.utils.UserSessionIdentifier
import io.socket.client.Socket
import javax.inject.Inject

class SocketSessionIdentifier @Inject constructor(private val socket: Socket) : UserSessionIdentifier {
    override fun getUserId(): String {
        return socket.id()
    }
}