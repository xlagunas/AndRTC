package cat.xlagunas.contact.data

import cat.xlagunas.contact.domain.ContactCache
import cat.xlagunas.contact.domain.TimeProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class ContactCacheImplTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var timeProvider: TimeProvider

    private val sharedPreferences = FakeSharedPreferences()

    private lateinit var contactCache: ContactCache

    private val fakeCurrentTime = System.currentTimeMillis()

    @Before
    fun setUp() {
        contactCache = ContactCacheImpl(sharedPreferences, timeProvider)
    }

    @Test
    fun givenMoreThanExactValidCacheMillisEllapsed_whenIsCacheValid_thenFalse() {
        sharedPreferences.edit()
            .putLong(
                ContactCacheImpl.LAST_MODIFIED_DATE,
                fakeCurrentTime - ContactCacheImpl.MAX_VALID_CACHE_MILLIS.plus(1),
            ).commit()
        `when`(timeProvider.getTimeMillis()).thenReturn(fakeCurrentTime)

        contactCache.isCacheValid()
            .test()
            .assertValue(false)
            .assertComplete()
    }

    @Test
    fun givenExactValidCacheMillisEllapsed_whenIsCacheValid_thenTrue() {
        sharedPreferences.edit()
            .putLong(
                ContactCacheImpl.LAST_MODIFIED_DATE,
                fakeCurrentTime - ContactCacheImpl.MAX_VALID_CACHE_MILLIS,
            ).commit()
        `when`(timeProvider.getTimeMillis()).thenReturn(fakeCurrentTime)

        contactCache.isCacheValid()
            .test()
            .assertValue(true)
            .assertComplete()
    }

    @Test
    fun givenLessThanValidCacheMillisEllapsed_whenIsCacheValid_thenTrue() {
        sharedPreferences.edit()
            .putLong(
                ContactCacheImpl.LAST_MODIFIED_DATE,
                fakeCurrentTime - ContactCacheImpl.MAX_VALID_CACHE_MILLIS.minus(1),
            ).commit()
        `when`(timeProvider.getTimeMillis()).thenReturn(fakeCurrentTime)

        contactCache.isCacheValid()
            .test()
            .assertValue(true)
            .assertComplete()
    }

    @Test
    fun givenCurrentTime_whenUpdateCache_thenLastModifiedDateStored() {
        val currentTime = 50000L
        `when`(timeProvider.getTimeMillis()).thenReturn(currentTime)

        contactCache.updateCache()
            .test().assertComplete()

        val lastModifiedDate = sharedPreferences.getLong(ContactCacheImpl.LAST_MODIFIED_DATE, 0)
        assertThat(lastModifiedDate).isEqualTo(currentTime)
    }

    @Test
    fun givenStoredDate_whenInvalidateCache_thenNoStoredValue() {
        sharedPreferences.edit().putLong(ContactCacheImpl.LAST_MODIFIED_DATE, 12345L).commit()

        contactCache.invalidateCache().test().assertComplete()

        assertThat(sharedPreferences.contains(ContactCacheImpl.LAST_MODIFIED_DATE)).isFalse()
        val lastModifiedDate = sharedPreferences.getLong(ContactCacheImpl.LAST_MODIFIED_DATE, -1)
        assertThat(lastModifiedDate).isEqualTo(-1)
    }
}
