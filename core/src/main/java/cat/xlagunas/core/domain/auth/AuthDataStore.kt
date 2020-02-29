package cat.xlagunas.core.domain.auth

import io.reactivex.subjects.BehaviorSubject

interface AuthDataStore {

    fun authToken(): String?

    fun isAuthTokenAvailable(): Boolean

    fun insertAuthToken(token: String)

    fun deleteAuthToken()

    fun getCurrentUserId(): Long

    fun updateCurrentUserId(userId: Long)

    fun getCurrentUserIdFlowable(): BehaviorSubject<Long>

    fun deleteUserPreferences()
}