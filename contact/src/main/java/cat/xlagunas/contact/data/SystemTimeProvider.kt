package cat.xlagunas.contact.data

import cat.xlagunas.contact.domain.TimeProvider

class SystemTimeProvider : TimeProvider {

    override fun getTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}
