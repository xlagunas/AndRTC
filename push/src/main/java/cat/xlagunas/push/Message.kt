package cat.xlagunas.push

sealed class Message {
    abstract val messageType: MessageType
}
    data class ContactMessage(override val messageType: MessageType) : Message()
    data class CallMessage(override val messageType: MessageType, val params: String) : Message()

enum class MessageType {
    CREATE_CALL, ACCEPT_CALL, REJECT_CALL, REQUEST_FRIENDSHIP, ACCEPT_FRIENDSHIP, REJECT_FRIENDSHIP
}
