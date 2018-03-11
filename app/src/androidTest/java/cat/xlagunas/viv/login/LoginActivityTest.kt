package cat.xlagunas.viv.login

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.WireMockTest
import com.github.tomakehurst.wiremock.client.WireMock
import junit.framework.Assert.assertTrue
import org.hamcrest.core.StringEndsWith.endsWith
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest : WireMockTest() {

    @get:Rule
    @JvmField
    val activityRule = ActivityTestRule(LoginActivity::class.java, true)


    @Test
    fun givenValidCredentials_whenUserLogsIn_thenLoginActivityFinishes() {
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo("/auth/"))
                .willReturn(
                        WireMock.aResponse()
                                .withBody(asset("login/successful_login.json"))
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")))

        activityRule.launchActivity(Intent())

        onView(ViewMatchers.withId(R.id.username_input_layout)).perform(ViewActions.typeText("aRandomValidUsername"))
        onView(ViewMatchers.withId(R.id.password_text_input)).perform(ViewActions.typeText("aRandomValidPassword"))
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click())

        assertTrue(activityRule.activity.isFinishing)

    }

    @Test
    fun givenInvalidCredentials_whenUserLogsIn_thenSnackbarShowsError() {
        wiremock.stubFor(WireMock.post(WireMock.urlEqualTo("/auth/"))
                .willReturn(
                        WireMock.aResponse().withBody(asset("login/invalid_login.json"))
                                .withStatus(401)
                                .withHeader("Content-Type", "application/json")))

        activityRule.launchActivity(Intent())

        onView(ViewMatchers.withId(R.id.username_input_layout)).perform(ViewActions.typeText("aRandomInvalidUsername"))
        onView(ViewMatchers.withId(R.id.password_text_input)).perform(ViewActions.typeText("aRandomInvalidPassword"))
        onView(ViewMatchers.withId(R.id.login_button)).perform(ViewActions.click())

        onView(ViewMatchers.withClassName(endsWith("Snackbar"))).check(matches(isDisplayed()))

    }
}