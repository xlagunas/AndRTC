package cat.xlagunas.test.utils.http

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.Test

class RouteTableTest {
    private val table = RouteTable()

    @Test
    fun givenTwoRoutesSamePath_whenMatched_thenLatestWins() {
        val first = FakeResponse(code = 200, body = "first")
        val second = FakeResponse(code = 200, body = "second")
        table.respondTo("GET", "/contact", first)
        table.respondTo("GET", "/contact", second)

        assertThat(table.match("GET", "/contact")).isEqualTo(second)
    }

    @Test
    fun givenRegexPath_whenPathDiffers_thenNoMatch() {
        table.respondTo("GET", "/contact/\\d+", FakeResponse(code = 200, body = "ok"))

        assertThat(table.match("GET", "/contact/42")).isEqualTo(FakeResponse(code = 200, body = "ok"))
        assertThat(table.match("GET", "/contact/abc")).isNull()
    }

    @Test
    fun givenMethodMismatch_whenMatched_thenNull() {
        table.respondTo("GET", "/contact", FakeResponse(code = 200, body = "ok"))

        assertThat(table.match("POST", "/contact")).isNull()
    }

    @Test
    fun givenRoutes_whenCleared_thenNoMatch() {
        table.respondTo("GET", "/contact", FakeResponse(code = 200, body = "ok"))

        table.clear()

        assertThat(table.match("GET", "/contact")).isNull()
    }
}
