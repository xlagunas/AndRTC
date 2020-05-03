package cat.xlagunas.core.common

sealed class Result<T>(val data: T? = null) {
    class Success<T>(data: T) : Result<T>(data)
    class Error<T>(data: T? = null) : Result<T>(data)
}
