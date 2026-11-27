package cat.xlagunas.contact.core

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.single
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.core.persistence.db.FriendEntity
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.http.FakeBackend
import cat.xlagunas.test.utils.http.FakeResponse
import cat.xlagunas.test.utils.http.disconnect
import cat.xlagunas.test.utils.http.httpError
import cat.xlagunas.test.utils.http.malformedJson
import cat.xlagunas.test.utils.http.ok
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContactRelationshipCoreTest {
    @get:Rule
    val rule = CoreTestRule()

    @get:Rule
    val backend = FakeBackend()

    private lateinit var harness: ContactCoreHarness

    // The friend found by the search fixture, not stored yet.
    private val jordina = Friend(12, "jordina", "Jordina", null, "jordina@example.com", "NONE")

    private val requestedJordina = FriendEntity(12, 3, "jordina", "Jordina", "jordina@example.com", "", "REQUESTED")
    private val acceptedMarta = marta.toEntity(3).copy(relationShipStatus = "ACCEPTED")

    @Before
    fun setUp() {
        harness = ContactCoreHarness(backend, rule)
    }

    @After
    fun tearDown() {
        harness.close()
    }

    @Test
    fun givenBackendAccepts_whenAddContact_thenRequestSentAndStoredAsRequested() {
        requestSeeded(ok())

        requestOutcome()
    }

    @Test
    fun givenBackendAccepts_whenAcceptContactRequest_thenRequestSentAndStoredAsAccepted() {
        acceptSeeded(ok())

        acceptOutcome()
    }

    @Test
    fun givenBackendAccepts_whenRejectContactRequest_thenRequestSentAndContactRemoved() {
        rejectSeeded(ok())

        rejectOutcome()
    }

    @Test
    fun givenUnauthorized_whenAddContact_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::requestSeeded, emptyList(), httpError(401))
    }

    @Test
    fun givenForbidden_whenAddContact_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::requestSeeded, emptyList(), httpError(403))
    }

    @Test
    fun givenNotFound_whenAddContact_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::requestSeeded, emptyList(), httpError(404))
    }

    @Test
    fun givenServerError_whenAddContact_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::requestSeeded, emptyList(), httpError(500))
    }

    @Test
    fun givenDisconnect_whenAddContact_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::requestSeeded, emptyList(), disconnect())
    }

    @Test
    fun givenMalformedBody_whenAddContact_thenTreatedAsSuccess() {
        requestSeeded(malformedJson())

        requestOutcome()
    }

    @Test
    fun givenUnauthorized_whenAcceptContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::acceptSeeded, listOf(marta.toEntity(3)), httpError(401))
    }

    @Test
    fun givenForbidden_whenAcceptContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::acceptSeeded, listOf(marta.toEntity(3)), httpError(403))
    }

    @Test
    fun givenNotFound_whenAcceptContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::acceptSeeded, listOf(marta.toEntity(3)), httpError(404))
    }

    @Test
    fun givenServerError_whenAcceptContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::acceptSeeded, listOf(marta.toEntity(3)), httpError(500))
    }

    @Test
    fun givenDisconnect_whenAcceptContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::acceptSeeded, listOf(marta.toEntity(3)), disconnect())
    }

    @Test
    fun givenMalformedBody_whenAcceptContactRequest_thenTreatedAsSuccess() {
        acceptSeeded(malformedJson())

        acceptOutcome()
    }

    @Test
    fun givenUnauthorized_whenRejectContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::rejectSeeded, listOf(marta.toEntity(3)), httpError(401))
    }

    @Test
    fun givenForbidden_whenRejectContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::rejectSeeded, listOf(marta.toEntity(3)), httpError(403))
    }

    @Test
    fun givenNotFound_whenRejectContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::rejectSeeded, listOf(marta.toEntity(3)), httpError(404))
    }

    @Test
    fun givenServerError_whenRejectContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::rejectSeeded, listOf(marta.toEntity(3)), httpError(500))
    }

    @Test
    fun givenDisconnect_whenRejectContactRequest_thenStoredContactsUnchanged() {
        assertStoredContactsUnchanged(::rejectSeeded, listOf(marta.toEntity(3)), disconnect())
    }

    @Test
    fun givenMalformedBody_whenRejectContactRequest_thenTreatedAsSuccess() {
        rejectSeeded(malformedJson())

        rejectOutcome()
    }

    private fun requestSeeded(response: FakeResponse) {
        harness.givenLoggedInUser()
        backend.respondTo("PUT", "/contact/12", response)
        harness.contactViewModel().addContact(jordina)
        harness.awaitCallsFinished()
    }

    private fun acceptSeeded(response: FakeResponse) {
        givenPendingMarta()
        backend.respondTo("POST", "/contact/11/accept", response)
        harness.contactViewModel().acceptContactRequest(marta)
        harness.awaitCallsFinished()
    }

    private fun rejectSeeded(response: FakeResponse) {
        givenPendingMarta()
        backend.respondTo("POST", "/contact/11/reject", response)
        harness.contactViewModel().rejectContactRequest(marta)
        harness.awaitCallsFinished()
    }

    private fun givenPendingMarta() {
        harness.givenLoggedInUser()
        harness.givenStoredFriends(3, marta.toEntity(3))
    }

    private fun requestOutcome() {
        assertRequestSent("PUT", "/contact/12")
        assertThat(harness.storedFriends(3)).single().isEqualTo(requestedJordina)
    }

    private fun acceptOutcome() {
        assertRequestSent("POST", "/contact/11/accept")
        assertThat(harness.storedFriends(3)).containsExactly(acceptedMarta)
    }

    private fun rejectOutcome() {
        assertRequestSent("POST", "/contact/11/reject")
        assertThat(harness.storedFriends(3)).isEmpty()
    }

    private fun assertRequestSent(
        method: String,
        path: String,
    ) {
        val request = backend.awaitRequest()
        assertThat(request.method).isEqualTo(method)
        assertThat(request.path).isEqualTo(path)
        assertThat(request.header("Authorization")).isEqualTo("token-123")
    }

    // Pins current behaviour, see #149
    // The user gets no feedback: no state is exposed and the error is only logged.
    private fun assertStoredContactsUnchanged(
        act: (FakeResponse) -> Unit,
        seeded: List<FriendEntity>,
        response: FakeResponse,
    ) {
        act(response)

        assertThat(harness.storedFriends(3)).isEqualTo(seeded)
        assertThat(harness.navigator.calls.values).isEmpty()
    }
}
