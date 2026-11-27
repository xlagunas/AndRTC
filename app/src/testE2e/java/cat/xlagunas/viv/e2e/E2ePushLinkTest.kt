package cat.xlagunas.viv.e2e

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.Test

class E2ePushLinkTest {
    @Test
    fun givenFriendRequestType_whenParsed_thenEventTypeOnly() {
        assertThat(pushData("REQUEST_FRIENDSHIP", null))
            .isEqualTo(mapOf("eventType" to "REQUEST_FRIENDSHIP"))
    }

    @Test
    fun givenCreateCallWithParams_whenParsed_thenBothKeys() {
        assertThat(pushData("CREATE_CALL", """{"room":"abc"}"""))
            .isEqualTo(mapOf("eventType" to "CREATE_CALL", "params" to """{"room":"abc"}"""))
    }

    @Test
    fun givenUnknownType_whenParsed_thenNull() {
        assertThat(pushData("NOT_A_TYPE", null)).isNull()
    }

    @Test
    fun givenMissingType_whenParsed_thenNull() {
        assertThat(pushData(null, "{}")).isNull()
    }
}
