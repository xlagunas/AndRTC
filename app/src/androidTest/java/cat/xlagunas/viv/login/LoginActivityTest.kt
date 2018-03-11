package cat.xlagunas.viv.login

import android.content.Intent
import android.support.annotation.IdRes
import android.support.design.internal.SnackbarContentLayout
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.EditText
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.WireMockTest
import com.github.tomakehurst.wiremock.client.WireMock
import junit.framework.Assert.assertTrue
import org.hamcrest.CoreMatchers.allOf
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

        onEditTextWithinTilWithId(R.id.username_input_layout).perform(typeText("aRandomValidUsername"), closeSoftKeyboard())
        onEditTextWithinTilWithId(R.id.password_text_input).perform(typeText("aRandomValidPassword"), closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.login_button)).perform(click())

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

        onEditTextWithinTilWithId(R.id.username_input_layout).perform(typeText("aRandomInvalidUsername"), closeSoftKeyboard())
        onEditTextWithinTilWithId(R.id.password_text_input).perform(typeText("aRandomInvalidPassword"), closeSoftKeyboard())
        onView(ViewMatchers.withId(R.id.login_button)).perform(click())

        onView(isAssignableFrom(SnackbarContentLayout::class.java)).check(matches(isDisplayed()))

    }

    private fun onEditTextWithinTilWithId(@IdRes textInputLayoutId: Int): ViewInteraction {
        //Note, if you have specified an ID for the EditText that you place inside
        //the TextInputLayout, use that instead - i.e, onView(withId(R.id.my_edit_text));
        return onView(allOf(isDescendantOfA(withId(textInputLayoutId)), isAssignableFrom(EditText::class.java)))
    }
}