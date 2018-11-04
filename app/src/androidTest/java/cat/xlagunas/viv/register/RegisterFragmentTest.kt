package cat.xlagunas.viv.register

import android.arch.lifecycle.MutableLiveData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import androidx.navigation.NavController
import cat.xlagunas.domain.commons.User
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.ViewModelUtil
import cat.xlagunas.viv.test.SingleFragmentActivity
import io.reactivex.Completable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RegisterFragmentTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)

    private val testFragment = TestRegisterFragment()
    private lateinit var registerViewModel: RegisterViewModel
    private val user = MutableLiveData<User>()

    @Before
    fun setUp() {
        registerViewModel = mock(RegisterViewModel::class.java)
        `when`(registerViewModel.findUser()).thenReturn(user)
        `when`(registerViewModel.register(getFakeUser())).thenReturn(Completable.complete())

        activityRule.activity.setFragment(testFragment)
        testFragment.viewModelFactory = ViewModelUtil.createFor(registerViewModel)
    }

    @Test
    fun givenRegisteredUser_WhenRegistering_ThenSuccess() {

        onView(withId(R.id.first_name_et)).perform(typeText("Anna Maria"))
        onView(withId(R.id.last_name)).perform(typeText("Calpe Valls"))
        onView(withId(R.id.email)).perform(typeText("anna.calpe@gmail.com"))
        onView(withId(R.id.username)).perform(typeText("acalpe"))
        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.fab)).perform(click())

        verify(testFragment.navController).navigate(R.id.action_register_successful)
    }

    @Test
    fun givenRegisteredUser_WhenRegistering_ThenError() {
        `when`(registerViewModel.register(getFakeUser())).thenReturn(Completable.error(IOException()))

        onView(withId(R.id.first_name_et)).perform(typeText("Anna Maria"))
        onView(withId(R.id.last_name)).perform(typeText("Calpe Valls"))
        onView(withId(R.id.email)).perform(typeText("anna.calpe@gmail.com"))
        onView(withId(R.id.username)).perform(typeText("acalpe"))
        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.fab)).perform(click())

        onView(withText("Error registering user")).check(matches(isDisplayed()))
    }

    private fun getFakeUser(): User =
        User(
            "acalpe",
            "Anna Maria",
            "Calpe Valls",
            "anna.calpe@gmail.com",
            "",
            "123456"
        )
}

class TestRegisterFragment : RegisterFragment() {
    val navController = Mockito.mock(NavController::class.java)
    override fun navController() = navController
}