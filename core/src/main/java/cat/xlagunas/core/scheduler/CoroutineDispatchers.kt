package cat.xlagunas.core.scheduler

import kotlinx.coroutines.CoroutineDispatcher

data class CoroutineDispatchers(val io: CoroutineDispatcher, val main: CoroutineDispatcher, val default: CoroutineDispatcher)
