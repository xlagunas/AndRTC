package cat.xlagunas.call.core

import cat.xlagunas.call.CallApi
import cat.xlagunas.call.CallConverter
import cat.xlagunas.call.CallRepositoryImpl
import cat.xlagunas.call.CallViewModel
import cat.xlagunas.core.network.HttpClientFactory
import cat.xlagunas.test.utils.CoreTestRule
import cat.xlagunas.test.utils.RecordingNavigator
import cat.xlagunas.test.utils.http.FakeBackend
import com.google.gson.Gson

/**
 * The call feature wired by hand with its production classes. Only HTTP ([FakeBackend])
 * and navigation are fakes.
 */
class CallCoreHarness(
    backend: FakeBackend,
    private val rule: CoreTestRule,
) {
    val navigator = RecordingNavigator()

    private val retrofit =
        HttpClientFactory.retrofit(
            backend.url,
            HttpClientFactory.okHttpClient(emptyList()),
            Gson(),
        )

    private val callRepository =
        CallRepositoryImpl(
            retrofit.create(CallApi::class.java),
            CallConverter(Gson()),
        )

    fun callViewModel() = CallViewModel(callRepository, navigator, rule.testDispatcher)
}
