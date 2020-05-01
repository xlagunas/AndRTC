package cat.xlagunas.push

import cat.xlagunas.core.domain.schedulers.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Maybe
import timber.log.Timber
import javax.inject.Inject

class PushTokenRepositoryImpl @Inject constructor(
    private val pushTokenProvider: PushTokenProvider,
    private val pushTokenApi: PushTokenApi,
    private val schedulers: RxSchedulers
) : PushTokenRepository {
    override fun invalidatePushToken(): Completable {
        return Completable.fromAction { pushTokenProvider.invalidatePushToken() }
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    override fun isPushTokenRegistered(): Boolean {
        return pushTokenProvider.isTokenRegistered()
    }

    override fun markPushTokenAsRegistered() {
        pushTokenProvider.markTokenAsRegistered()
    }

    override fun addPushToken(token: PushTokenDto): Completable {
        return pushTokenApi.addPushToken(token)
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    override fun registerPushToken(): Completable {
        return getTokenFromTokenProvider()
            .flatMapCompletable(this::requestTokenRegistration)
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }

    private fun requestTokenRegistration(token: String): Completable {
        return addPushToken(PushTokenDto(token))
            .doOnComplete { pushTokenProvider.markTokenAsRegistered() }
            .doOnComplete { Timber.d("Push token successfully registered") }
            .doOnSubscribe { Timber.d("Starting token registration") }
    }

    private fun getTokenFromTokenProvider(): Maybe<String> {
        return Maybe.fromCallable { pushTokenProvider.getPushToken() }
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.io)
    }
}