package cat.xlagunas.contact.domain

interface TimeProvider {

    fun getTimeMillis(): Long
}