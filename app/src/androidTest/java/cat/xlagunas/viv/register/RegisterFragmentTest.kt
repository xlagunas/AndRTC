package cat.xlagunas.viv.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.ViewModelUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class RegisterFragmentTest {

    private val registerViewModel = mock(RegisterViewModel::class.java)
    private val user = MutableLiveData<User>()
    private val registrationState = MutableLiveData<RegistrationState>()
    private lateinit var registerFragmentFactory: FragmentFactory

    @Before
    fun setUp() {
        `when`(registerViewModel.findUser()).thenReturn(user)
        `when`(registerViewModel.registrationState).thenReturn(registrationState)
        registerFragmentFactory = TestRegisterFragmentFactory(ViewModelUtil.createFor(registerViewModel))
    }

    @Test
    fun givenRegisteredUser_WhenRegistering_ThenSuccess() {

        doAnswer {
            registrationState.postValue(Success)
            Unit
        }.`when`(registerViewModel).register(getFakeUser())

        val scenario = launchFragmentInContainer<TestRegisterFragment>(Bundle(), R.style.AppTheme_NoActionBar, registerFragmentFactory)

        onView(withId(R.id.first_name_et)).perform(typeText("Anna Maria"))
        onView(withId(R.id.last_name)).perform(typeText("Calpe Valls"))
        onView(withId(R.id.email)).perform(typeText("anna.calpe@gmail.com"))
        onView(withId(R.id.username)).perform(typeText("acalpe"))
        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.fab)).perform(click())

        scenario.onFragment {
            verify(it.navController()).navigate(R.id.action_register_successful)
        }
    }

    @Test
    fun givenRegisteredUser_WhenRegistering_ThenError() {

        doAnswer {
            registrationState.postValue(RegistrationError("Invalid account data"))
            Unit
        }.`when`(registerViewModel).register(getFakeUser())

        launchFragmentInContainer<TestRegisterFragment>(Bundle(), R.style.AppTheme_NoActionBar, registerFragmentFactory)

        onView(withId(R.id.first_name_et)).perform(typeText("Anna Maria"))
        onView(withId(R.id.last_name)).perform(typeText("Calpe Valls"))
        onView(withId(R.id.email)).perform(typeText("anna.calpe@gmail.com"))
        onView(withId(R.id.username)).perform(typeText("acalpe"))
        onView(withId(R.id.password)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.fab)).perform(click())

        onView(withText("Error registering user Invalid account data")).check(matches(isDisplayed()))
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
    override fun inject() {}
}

class TestRegisterFragmentFactory(private val registerViewModelFactory: ViewModelProvider.Factory): FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String, args: Bundle?): Fragment {
        return TestRegisterFragment().apply { viewModelFactory = registerViewModelFactory }
    }
}