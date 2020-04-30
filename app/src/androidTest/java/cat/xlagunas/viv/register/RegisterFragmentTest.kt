package cat.xlagunas.viv.register

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.test.utils.ViewModelUtil
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.TestApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class RegisterFragmentTest {

    private val registerViewModel = mock(RegisterViewModel::class.java)
    private val user = MutableLiveData<User>()
    private val registrationState = MutableLiveData<RegistrationError>()

    @Before
    fun setUp() {
        val application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
        application.setViewModelProviderFactory(ViewModelUtil.createFor(registerViewModel))
        `when`(registerViewModel.findUser()).thenReturn(user)
        `when`(registerViewModel.onRegistrationError).thenReturn(registrationState)
    }

    @Test
    fun givenRegisteredUser_WhenRegistering_ThenError() {

        doAnswer {
            registrationState.postValue(RegistrationError("Invalid account data"))
            Unit
        }.`when`(registerViewModel).register(getFakeUser())

        launchFragmentInContainer<RegisterFragment>(Bundle(), R.style.AppTheme_NoActionBar)

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