package cat.xlagunas.viv.login

import android.os.Bundle
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import cat.xlagunas.viv.R
import cat.xlagunas.core.di.ViewModelUtil
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
    private lateinit var testLoginFactory: TestLoginFactory

    @Before
    fun setUp() {
        `when`(loginViewModel.onLoginStateChange()).thenReturn(loginState)

        testLoginFactory = TestLoginFactory(ViewModelUtil.createFor(loginViewModel))
    }

    @Test
    fun givenValidCredentials_whenUserLogsIn_thenLoggedUserFragment() {
        doAnswer {
            loginState.postValue(SuccessLoginState)
            Unit
        }.`when`(loginViewModel).login(anyString(), anyString())

        val scenario = launchFragmentInContainer<TestLoginFragment>(
            Bundle(),
            R.style.AppTheme_NoActionBar,
            testLoginFactory
        )

        onEditTextWithinTilWithId(R.id.username_input_layout).perform(
            typeText("aRandomValidUsername"),
            closeSoftKeyboard()
        )
        onEditTextWithinTilWithId(R.id.password_text_input).perform(
            typeText("aRandomValidPassword"),
            closeSoftKeyboard()
        )

        onView(ViewMatchers.withId(R.id.login_button)).perform(click())

        scenario.onFragment { verify(it.navController).popBackStack() }
    }

    @Test
    fun givenInvalidCredentials_whenUserLogsIn_thenSnackbarShowsError() {
        doAnswer {
            loginState.postValue(InvalidLoginState("Authentication error"))
            Unit
        }.`when`(loginViewModel).login(anyString(), anyString())

        launchFragmentInContainer<TestLoginFragment>(
            Bundle(),
            R.style.AppTheme_NoActionBar,
            testLoginFactory
        )

        onEditTextWithinTilWithId(R.id.username_input_layout).perform(
            typeText("aRandomInvalidUsername"),
            closeSoftKeyboard()
        )
        onEditTextWithinTilWithId(R.id.password_text_input).perform(
            typeText("aRandomInvalidPassword"),
            closeSoftKeyboard()
        )
        onView(ViewMatchers.withId(R.id.login_button)).perform(click())

        onView(isAssignableFrom(SnackbarContentLayout::class.java)).check(matches(isDisplayed()))
    }

    @Test
    fun whenUserClickRegister_thenNavigateToRegister() {
        val scenario = launchFragmentInContainer<TestLoginFragment>(
            Bundle(),
            R.style.AppTheme_NoActionBar,
            testLoginFactory
        )

        onView(ViewMatchers.withId(R.id.register)).perform(click())

        scenario.onFragment { verify(it.navController).navigate(R.id.action_login_to_register) }
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

    class TestLoginFragment : LoginFragment() {
        val navController = mock(NavController::class.java)
        override fun navController() = navController
    }

    class TestLoginFactory(private val loginViewModelFactory: ViewModelProvider.Factory) :
        FragmentFactory() {
        override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
            return TestLoginFragment().apply { viewModelFactory = loginViewModelFactory }
        }
    }
}