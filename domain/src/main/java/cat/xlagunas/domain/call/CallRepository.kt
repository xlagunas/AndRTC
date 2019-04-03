package cat.xlagunas.domain.call

import cat.xlagunas.core.domain.entity.Friend
import io.reactivex.Completable
import io.reactivex.Single

interface CallRepository {
    fun createCall(contacts: List<Friend>): Single<Call>
    fun acceptCall(callId: Long): Completable
    fun rejectCall(callId: Long): Completable
}