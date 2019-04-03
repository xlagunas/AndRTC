package cat.xlagunas.data.call

import cat.xlagunas.core.data.converter.FriendConverter
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.core.domain.schedulers.RxSchedulers
import cat.xlagunas.domain.call.Call
import cat.xlagunas.domain.call.CallRepository
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

    override fun acceptCall(callId: Long): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun rejectCall(callId: Long): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}