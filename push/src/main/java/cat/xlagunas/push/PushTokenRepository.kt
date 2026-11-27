package cat.xlagunas.push

import io.reactivex.Completable

interface PushTokenRepository {
    fun addPushToken(token: PushTokenDto): Completable

    fun registerPushToken(): Completable

    /** Sends [token] to the backend; marks it registered on success, clears the flag on error. */
    fun registerPushToken(token: String): Completable

    fun markPushTokenAsRegistered()

    fun isPushTokenRegistered(): Boolean

    fun invalidatePushToken(): Completable
}
