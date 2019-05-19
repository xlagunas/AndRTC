package cat.xlagunas.call

import cat.xlagunas.core.data.converter.FriendConverter
import cat.xlagunas.core.domain.entity.Call
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.push.CallMessage
import cat.xlagunas.push.Message
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class CallRepositoryImpl @Inject constructor(
    private val callApi: CallApi,
    private val friendConverter: FriendConverter,
    private val callConverter: CallConverter,
    private val schedulers: RxSchedulers
) : CallRepository {

    override fun createCall(contacts: List<Friend>): Single<Call> {
        val friendsList = friendConverter.toFriendDtoList(contacts)
        val callParticipantsDto = callConverter.toCallParticipantsDto(friendsList)

        return callApi.createCall(callParticipantsDto)
            .map(callConverter::toCall)
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    override fun getCallDetails(message: Message): Single<Call> {
        return Single.just(callConverter.toCall(message as CallMessage))
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    override fun acceptCall(callId: Long): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun rejectCall(callId: Long): Completable {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }
}