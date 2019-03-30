package cat.xlagunas.core.domain.time

interface TimeProvider {

    fun getTimeMillis(): Long
}