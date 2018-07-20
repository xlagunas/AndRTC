package cat.xlagunas.viv.login

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.IdRes
import android.support.design.widget.SnackbarContentLayout
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.EditText
import androidx.navigation.NavController
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.ViewModelUtil
import cat.xlagunas.viv.test.SingleFragmentActivity
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
        //Note, if you have specified an ID for the EditText that you place inside
        //the TextInputLayout, use that instead - i.e, onView(withId(R.id.my_edit_text));
        return onView(allOf(isDescendantOfA(withId(textInputLayoutId)), isAssignableFrom(EditText::class.java)))
    }

    class TestLoginFragment : LoginFragment() {
        val navController = Mockito.mock(NavController::class.java)
        override fun navController() = navController
    }
}