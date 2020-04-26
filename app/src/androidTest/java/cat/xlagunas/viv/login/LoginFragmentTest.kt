package cat.xlagunas.viv.login

import android.os.Bundle
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cat.xlagunas.test_utils.ViewModelUtil
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.TestApplication
import com.google.android.material.snackbar.SnackbarContentLayout
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    private val loginViewModel = mock(LoginViewModel::class.java)
    private val loginState = MutableLiveData<LoginState>()

    @Before
    fun setUp() {
        val application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
        application.setViewModelProviderFactory(ViewModelUtil.createFor(loginViewModel))
        `when`(loginViewModel.onLoginStateChange()).thenReturn(loginState)
    }

    @Test
    fun givenInvalidCredentials_whenUserLogsIn_thenSnackbarShowsError() {
        doAnswer {
            loginState.postValue(InvalidLoginState("Authentication error"))
            Unit
        }.`when`(loginViewModel).login(anyString(), anyString())

        launchFragmentInContainer<LoginFragment>(
            Bundle(),
            R.style.AppTheme_NoActionBar
        )

        onEditTextWithinTilWithId(R.id.username_input_layout).perform(
            typeText("aRandomInvalidUsername"),
            closeSoftKeyboard()
        )
        onEditTextWithinTilWithId(R.id.password_text_input).perform(
            typeText("aRandomInvalidPassword"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.login_button)).perform(click())

        onView(isAssignableFrom(SnackbarContentLayout::class.java)).check(matches(isDisplayed()))
    }

    @Test
    fun whenUserClickRegister_thenNavigateToRegister() {
        val scenario = launchFragmentInContainer<LoginFragment>(
            Bundle(),
            R.style.AppTheme_NoActionBar
        )

        onView(withId(R.id.register)).perform(click())

        scenario.onFragment { verify(loginViewModel).navigateToRegistration() }
    }

    private fun onEditTextWithinTilWithId(@IdRes textInputLayoutId: Int): ViewInteraction {
        // Note, if you have specified an ID for the EditText that you place inside
        // the TextInputLayout, use that instead - i.e, onView(withId(R.id.my_edit_text));
        return onView(
            allOf(
                isDescendantOfA(withId(textInputLayoutId)),
                isAssignableFrom(EditText::class.java)
            )
        )
    }
}