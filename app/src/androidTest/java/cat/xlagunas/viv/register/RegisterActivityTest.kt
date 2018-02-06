package cat.xlagunas.viv.register

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.DaggerApplicationTestComponent
import cat.xlagunas.viv.commons.di.VivApplication
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit.WireMockClassRule
import com.jakewharton.espresso.OkHttp3IdlingResource
import junit.framework.Assert.assertTrue
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


@RunWith(AndroidJUnit4::class)
class RegisterActivityTest {

    @get:Rule
    @JvmField
    val activityRule = ActivityTestRule(RegisterActivity::class.java, true)

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

    @Test
    fun givenRegisteredUser_WhenRegistering_ThenSuccess() {

        wiremock.stubFor(any(urlEqualTo("/user/"))
                .willReturn(
                        aResponse().withBody("{}")
                                .withStatus(201)
                                .withHeader("Content-Type", "application/json")
                                .withHeader("Connection", "close")))

        activityRule.launchActivity(Intent())

        onView(withId(R.id.first_name_et)).perform(typeText("Anna Maria"))
        onView(withId(R.id.last_name)).perform(typeText("Calpe Valls"))
        onView(withId(R.id.email)).perform(typeText("anna.calpe@gmail.com"))
        onView(withId(R.id.username)).perform(typeText("acalpe"))
        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.fab)).perform(click())
        assertTrue(activityRule.activity.isFinishing)
    }

    @Test
    fun givenRegisteredUser_WhenRegistering_ThenError() {
        wiremock.stubFor(put(urlEqualTo("/user/"))
                .willReturn(aResponse()
                        .withBody(asset("register/duplicated.json"))
                        .withStatus(409)
                        .withHeader("Content-Type", "application/json")))

        activityRule.launchActivity(Intent())

        onView(withId(R.id.first_name_et)).perform(typeText("Anna Maria"))
        onView(withId(R.id.last_name)).perform(typeText("Calpe Valls"))
        onView(withId(R.id.email)).perform(typeText("anna.calpe@gmail.com"))
        onView(withId(R.id.username)).perform(typeText("acalpe"))
        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.fab)).perform(click())
        onView(withText("Error registering user")).check(matches(isDisplayed()))
    }

    fun asset(assetPath: String): String {
        val context = getInstrumentation().context
        try {
            val inputStream = context.assets.open(assetPath)
            return inputStreamToString(inputStream, "UTF-8")
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    private val BUFFER_SIZE = 4 * 1024
    @Throws(IOException::class)
    private fun inputStreamToString(inputStream: InputStream, charsetName: String): String {
        val builder = StringBuilder()
        val reader = InputStreamReader(inputStream, charsetName)
        val buffer = CharArray(BUFFER_SIZE)
        var length = reader.read(buffer)
        while (length != -1) {
            builder.append(buffer, 0, length)
            length = reader.read(buffer)
        }
        return builder.toString()
    }
}