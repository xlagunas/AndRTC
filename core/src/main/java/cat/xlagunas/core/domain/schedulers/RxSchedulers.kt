package cat.xlagunas.core.domain.schedulers

import io.reactivex.Scheduler

data class RxSchedulers(val io: Scheduler, val mainThread: Scheduler, val computation: Scheduler)
