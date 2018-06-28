package cat.xlagunas.domain.schedulers

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class RxSchedulers(val io: Scheduler, val mainThread: Scheduler, val computation: Scheduler) {

    companion object {
        val TEST_SCHEDULERS = RxSchedulers(Schedulers.trampoline(), Schedulers.trampoline(), Schedulers.trampoline())
    }
}
