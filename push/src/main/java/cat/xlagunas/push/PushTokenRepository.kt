package cat.xlagunas.push

import io.reactivex.Completable

interface PushTokenRepository {
    fun addPushToken(token: PushTokenDto): Completable
    fun registerPushToken(): Completable
    fun markPushTokenAsRegistered()
    fun isPushTokenRegistered(): Boolean
    fun invalidatePushToken(): Completable
}
