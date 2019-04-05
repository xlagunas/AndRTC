package cat.xlagunas.viv.push

interface MessageProcessor {
    fun processMessage(message: Message)
}