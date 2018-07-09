package cat.xlagunas.domain.common.time

interface TimeProvider {

    fun getTimeMillis(): Long
}