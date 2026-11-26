package cat.xlagunas.test.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RecordingNavigatorTest {
    @Test
    fun givenNavigatorCalls_whenRecorded_thenInOrder() {
        val navigator = RecordingNavigator()

        navigator.navigateToProfile()
        navigator.startCall("r1")
        navigator.requestCall(5L, "Anna")

        assertThat(navigator.calls.values).containsExactly(
            NavigationCall.ToProfile,
            NavigationCall.StartCall("r1"),
            NavigationCall.RequestCall(5L, "Anna"),
        )
    }
}
