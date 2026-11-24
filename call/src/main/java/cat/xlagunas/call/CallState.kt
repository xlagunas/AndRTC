package cat.xlagunas.call

sealed class CallState {
    data object Idle : CallState()

    data object Creating : CallState()

    data object Error : CallState()

    data object Started : CallState()
}
