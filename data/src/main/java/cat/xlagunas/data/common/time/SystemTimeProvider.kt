package cat.xlagunas.data.common.time

import cat.xlagunas.domain.common.time.TimeProvider

class SystemTimeProvider : TimeProvider {

    override fun getTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}