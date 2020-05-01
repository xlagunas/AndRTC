package cat.xlagunas.core.push

interface MessageProcessor {
    fun processMessage(message: cat.xlagunas.core.push.Message)
}