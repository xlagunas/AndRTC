package cat.xlagunas.domain.schedulers

import io.reactivex.Scheduler

class RxSchedulers(val io: Scheduler, val mainThread: Scheduler, val computation: Scheduler)
