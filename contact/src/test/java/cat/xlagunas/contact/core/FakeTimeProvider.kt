package cat.xlagunas.contact.core

import cat.xlagunas.contact.domain.TimeProvider

class FakeTimeProvider(var now: Long = 1_700_000_000_000L) : TimeProvider {
    override fun getTimeMillis(): Long = now
}
