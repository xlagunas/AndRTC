package cat.xlagunas.viv.register

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.WireMockTest
import com.github.tomakehurst.wiremock.client.WireMock.*
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RegisterActivityTest : WireMockTest() {

    @get:Rule
    @JvmField
    val activityRule = ActivityTestRule(RegisterActivity::class.java, true)

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

}