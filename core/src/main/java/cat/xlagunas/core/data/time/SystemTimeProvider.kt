package cat.xlagunas.core.data.time

import cat.xlagunas.core.domain.time.TimeProvider

class SystemTimeProvider : TimeProvider {

    override fun getTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}