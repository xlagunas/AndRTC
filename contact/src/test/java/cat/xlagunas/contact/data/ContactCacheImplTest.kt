package cat.xlagunas.contact.data

import android.content.Context
import android.content.SharedPreferences
import cat.xlagunas.contact.data.ContactCacheImpl
import cat.xlagunas.contact.domain.ContactCache
import cat.xlagunas.core.domain.time.TimeProvider
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ContactCacheImplTest {

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var timeProvider: TimeProvider

    private lateinit var contactCache: ContactCache

    private val fakeCurrentTime = System.currentTimeMillis()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        contactCache = ContactCacheImpl(sharedPreferences, timeProvider)
    }

    @Test
    fun givenMoreThanExactValidCacheMillisEllapsed_whenIsCacheValid_thenFalse() {
        `when`(
            sharedPreferences.getLong(ContactCacheImpl.LAST_MODIFIED_DATE, 0)
        ).thenReturn(fakeCurrentTime - ContactCacheImpl.MAX_VALID_CACHE_MILLIS.plus(1))
        `when`(timeProvider.getTimeMillis()).thenReturn(fakeCurrentTime)

        contactCache.isCacheValid()
            .test()
            .assertValue(false)
            .assertComplete()
    }

    @Test
    fun givenExactValidCacheMillisEllapsed_whenIsCacheValid_thenTrue() {
        `when`(
            sharedPreferences.getLong(ContactCacheImpl.LAST_MODIFIED_DATE, 0)
        ).thenReturn(fakeCurrentTime - ContactCacheImpl.MAX_VALID_CACHE_MILLIS)
        `when`(timeProvider.getTimeMillis()).thenReturn(fakeCurrentTime)

        contactCache.isCacheValid()
            .test()
            .assertValue(true)
            .assertComplete()
    }

    @Test
    fun givenLessThanValidCacheMillisEllapsed_whenIsCacheValid_thenTrue() {
        `when`(
            sharedPreferences.getLong(ContactCacheImpl.LAST_MODIFIED_DATE, 0)
        ).thenReturn(fakeCurrentTime - ContactCacheImpl.MAX_VALID_CACHE_MILLIS.minus(1))
        `when`(timeProvider.getTimeMillis()).thenReturn(fakeCurrentTime)

        contactCache.isCacheValid()
            .test()
            .assertValue(true)
            .assertComplete()
    }

    @Test
    fun whenUpdateCache_thenSharedPreferenceUpdated() {
        val sharedPreferences =
            RuntimeEnvironment.application.getSharedPreferences("sp", Context.MODE_PRIVATE)
        val fakeCurrentTime = 50000L
        contactCache = ContactCacheImpl(sharedPreferences, timeProvider)
        `when`(timeProvider.getTimeMillis()).thenReturn(fakeCurrentTime)

        contactCache.updateCache()
            .test().assertComplete()

        val lastModifiedDate = sharedPreferences.getLong(ContactCacheImpl.LAST_MODIFIED_DATE, 0)
        assertThat(lastModifiedDate).isEqualTo(fakeCurrentTime)
    }

    @Test
    fun whenInvalidateCache_thenNoSharedPreferenceValue() {
        val sharedPreferences =
            RuntimeEnvironment.application.getSharedPreferences("sp", Context.MODE_PRIVATE)
        contactCache = ContactCacheImpl(sharedPreferences, timeProvider)

        contactCache.invalidateCache().test().assertComplete()

        val lastModifiedDate = sharedPreferences.getLong(ContactCacheImpl.LAST_MODIFIED_DATE, -1)
        assertThat(lastModifiedDate).isEqualTo(-1)
    }
}