package cat.xlagunas.test.utils

import cat.xlagunas.push.PushTokenProvider

/**
 * Stands in for Firebase Messaging. Set [failure] to make token access throw, as when
 * Google Play services or the network are unavailable.
 */
class FakePushTokenProvider(var token: String = "push-token-1") : PushTokenProvider {
    @Volatile
    var failure: Throwable? = null

    @Volatile
    private var registered = false

    @Volatile
    var invalidations = 0
        private set

    /** Every change of the registered flag: `true` when marked, `false` when cleared. */
    val registrationChanges = StateRecorder<Boolean>()

    override fun getPushToken(): String {
        failure?.let { throw it }
        return token
    }

    override fun isTokenRegistered(): Boolean = registered

    override fun markTokenAsRegistered() {
        registered = true
        registrationChanges.record(true)
    }

    override fun clearTokenRegistration() {
        registered = false
        registrationChanges.record(false)
    }

    override fun invalidatePushToken() {
        failure?.let { throw it }
        invalidations++
        registered = false
    }
}
