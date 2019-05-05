package cat.xlagunas.call

import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.push.Message
import io.reactivex.Completable
import io.reactivex.Single

interface CallRepository {
    fun createCall(contacts: List<Friend>): Single<Call>
    fun acceptCall(callId: Long): Completable
    fun rejectCall(callId: Long): Completable
    fun getCallDetails(message: Message): Single<Call>
}