package cat.xlagunas.viv.commons

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.IdlingRegistry
import cat.xlagunas.viv.commons.di.DaggerApplicationTestComponent
import cat.xlagunas.viv.commons.di.VivApplication
import com.github.tomakehurst.wiremock.junit.WireMockClassRule
import com.jakewharton.espresso.OkHttp3IdlingResource
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.ClassRule
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

open class WireMockTest {

    private lateinit var resource: OkHttp3IdlingResource

    companion object {

        @get:ClassRule
        @JvmStatic
        val wiremock = WireMockClassRule(9998)

        @BeforeClass
        @JvmStatic
        fun initServer() {
            val okHttpClient = OkHttpClient()
            val request = Request.Builder()
                    .url("http://127.0.0.1:9998/").put(RequestBody.create(MediaType.parse("application/json"), "{}"))
                    .build()

            okHttpClient.newCall(request).execute()

        }
    }

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val app = instrumentation.targetContext.applicationContext as VivApplication

        val testComponent = DaggerApplicationTestComponent.builder()
                .withApplication(app)
                .build()

        app.applicationComponent = testComponent


        resource = OkHttp3IdlingResource.create("OkHttp", testComponent.client())
        IdlingRegistry.getInstance().register(resource)
    }


    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(resource)
    }

    fun asset(assetPath: String): String {
        val context = InstrumentationRegistry.getInstrumentation().context
        try {
            val inputStream = context.assets.open(assetPath)
            return inputStreamToString(inputStream, "UTF-8")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    @Throws(IOException::class)
    private fun inputStreamToString(inputStream: InputStream, charsetName: String): String {
        val builder = StringBuilder()
        val reader = InputStreamReader(inputStream, charsetName)
        val buffer = CharArray(1024 * 4)
        var length = reader.read(buffer)
        while (length != -1) {
            builder.append(buffer, 0, length)
            length = reader.read(buffer)
        }
        return builder.toString()
    }

}