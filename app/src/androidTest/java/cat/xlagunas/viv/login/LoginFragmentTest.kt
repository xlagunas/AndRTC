package cat.xlagunas.viv.login

import android.widget.EditText
import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
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
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.ViewModelUtil
import cat.xlagunas.viv.test.SingleFragmentActivity
import com.google.android.material.snackbar.SnackbarContentLayout
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)

    private val testFragment = TestLoginFragment()
    private lateinit var loginViewModel: LoginViewModel
    private val loginState = MutableLiveData<LoginState>()

    @Before
    fun setUp() {
        loginViewModel = mock(LoginViewModel::class.java)
        `when`(loginViewModel.registerGoogle()).thenReturn(mock(LifecycleObserver::class.java))
        `when`(loginViewModel.onLoginStateChange()).thenReturn(loginState)

        activityRule.activity.setFragment(testFragment)
        testFragment.viewModelFactory = ViewModelUtil.createFor(loginViewModel)
    }

    @Test
    fun givenValidCredentials_whenUserLogsIn_thenLoginActivityFinishes() {
        doAnswer {
            loginState.postValue(SuccessLoginState)
            Unit
        }.`when`(loginViewModel).login(anyString(), anyString())

        onEditTextWithinTilWithId(R.id.username_input_layout).perform(
            typeText("aRandomValidUsername"),
            closeSoftKeyboard()
        )
        onEditTextWithinTilWithId(R.id.password_text_input).perform(
            typeText("aRandomValidPassword"),
            closeSoftKeyboard()
        )

        onView(ViewMatchers.withId(R.id.login_button)).perform(click())

        verify(testFragment.navController).popBackStack()
    }

    @Test
    fun givenInvalidCredentials_whenUserLogsIn_thenSnackbarShowsError() {
        doAnswer {
            loginState.postValue(InvalidLoginState("Authentication error"))
            Unit
        }.`when`(loginViewModel).login(anyString(), anyString())

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
        onView(ViewMatchers.withId(R.id.register)).perform(click())

        verify(testFragment.navController).navigate(R.id.action_login_to_register)
    }

    private fun onEditTextWithinTilWithId(@IdRes textInputLayoutId: Int): ViewInteraction {
        // Note, if you have specified an ID for the EditText that you place inside
        // the TextInputLayout, use that instead - i.e, onView(withId(R.id.my_edit_text));
        return onView(allOf(isDescendantOfA(withId(textInputLayoutId)), isAssignableFrom(EditText::class.java)))
    }

    class TestLoginFragment : LoginFragment() {
        val navController = Mockito.mock(NavController::class.java)
        override fun navController() = navController
    }
}