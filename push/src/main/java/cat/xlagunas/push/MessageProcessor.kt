package cat.xlagunas.push

interface MessageProcessor {
    fun processMessage(message: Message)
}