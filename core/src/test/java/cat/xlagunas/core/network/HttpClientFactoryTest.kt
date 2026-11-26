package cat.xlagunas.core.network

import com.google.gson.Gson
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.http.GET

class HttpClientFactoryTest {
    private val server = MockWebServer()

    @Before
    fun setUp() {
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun givenJsonResponse_whenCallingThroughRetrofit_thenParsesWithGson() {
        server.enqueue(MockResponse().setBody("""{"value":"hi"}"""))

        val result = api().echo().blockingGet()

        assertThat(result).isEqualTo(EchoDto("hi"))
    }

    @Test
    fun givenInterceptors_whenRequest_thenAppliedInOrder() {
        server.enqueue(MockResponse().setBody("""{"value":"hi"}"""))
        val first = headerInterceptor("1")
        val second = headerInterceptor("2")

        api(listOf(first, second)).echo().blockingGet()

        assertThat(server.takeRequest().headers.values("X-Order")).containsExactly("1", "2")
    }

    @Test
    fun givenSuspendFunction_whenCalled_thenWorks() {
        server.enqueue(MockResponse().setBody("""{"value":"hi"}"""))

        val result = runBlocking { api().echoSuspend() }

        assertThat(result).isEqualTo(EchoDto("hi"))
    }

    private fun api(interceptors: List<Interceptor> = emptyList()): EchoApi =
        HttpClientFactory
            .retrofit(server.url("/").toString(), HttpClientFactory.okHttpClient(interceptors), Gson())
            .create(EchoApi::class.java)

    private fun headerInterceptor(value: String) =
        Interceptor { chain ->
            chain.proceed(chain.request().newBuilder().addHeader("X-Order", value).build())
        }

    interface EchoApi {
        @GET("/echo/")
        fun echo(): Single<EchoDto>

        @GET("/echo/")
        suspend fun echoSuspend(): EchoDto
    }

    data class EchoDto(val value: String)
}
