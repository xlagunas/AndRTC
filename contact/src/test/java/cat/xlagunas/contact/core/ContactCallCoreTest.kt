package cat.xlagunas.contact.core

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.NavigationCall
import cat.xlagunas.test.utils.http.FakeBackend
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactCallCoreTest {
    @get:Rule
    val rule = CoreTestRule()

    @get:Rule
    val backend = FakeBackend()

    private lateinit var harness: ContactCoreHarness

    @Before
    fun setUp() {
        harness = ContactCoreHarness(backend, rule)
    }

    @After
    fun tearDown() {
        harness.close()
    }

    @Test
    fun givenFriends_whenCreateCall_thenRequestsCallToFirstFriend() {
        harness.contactViewModel().createCall(listOf(jordi, marta))

        assertThat(harness.navigator.calls.values).containsExactly(NavigationCall.RequestCall(10, "jordi"))
        assertThat(backend.requests).isEmpty()
    }

    @Test
    fun givenOneFriend_whenCreateCall_thenRequestsCallToIt() {
        harness.contactViewModel().createCall(listOf(marta))

        assertThat(harness.navigator.calls.values).containsExactly(NavigationCall.RequestCall(11, "marta"))
        assertThat(backend.requests).isEmpty()
    }

    @Test
    fun givenNoFriends_whenCreateCall_thenNoNavigation() {
        harness.contactViewModel().createCall(emptyList())

        assertThat(harness.navigator.calls.values).isEmpty()
        assertThat(backend.requests).isEmpty()
    }
}
