package cat.xlagunas.test.utils

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import org.junit.Test
import java.io.IOException

class FakePushTokenProviderTest {
    @Test
    fun givenToken_whenGetPushToken_thenReturnsIt() {
        val provider = FakePushTokenProvider(token = "push-1")

        assertThat(provider.getPushToken()).isEqualTo("push-1")
    }

    @Test
    fun givenFailure_whenGetPushToken_thenThrows() {
        val provider = FakePushTokenProvider().apply { failure = IOException("firebase down") }

        assertFailure { provider.getPushToken() }.isInstanceOf(IOException::class)
    }

    @Test
    fun givenRegistered_whenInvalidated_thenNotRegisteredAndCounted() {
        val provider = FakePushTokenProvider()
        provider.markTokenAsRegistered()

        provider.invalidatePushToken()

        assertThat(provider.isTokenRegistered()).isFalse()
        assertThat(provider.invalidations).isEqualTo(1)
    }
}
