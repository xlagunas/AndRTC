package cat.xlagunas.viv.contact

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import androidx.navigation.NavController
import cat.xlagunas.data.common.net.Relationship
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.ViewModelUtil
import cat.xlagunas.viv.push.PushTokenPresenter
import cat.xlagunas.viv.test.SingleFragmentActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class ContactFragmentTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private val testFragment = TestContactFragment()
    private lateinit var contactViewModel: ContactViewModel
    private lateinit var pushTokenPresenter: PushTokenPresenter

    private val contactsLiveData = MutableLiveData<List<Friend>>()

    @Before
    fun setUp() {
        contactViewModel = mock(ContactViewModel::class.java)
        pushTokenPresenter = mock(PushTokenPresenter::class.java)

        val contactViewModelFactory = ViewModelUtil.createFor(contactViewModel)

        testFragment.viewModelFactory = contactViewModelFactory
        testFragment.pushTokenPresenter = pushTokenPresenter

        activityRule.activity.setFragment(testFragment)

        `when`(pushTokenPresenter.isPushTokenRegistered()).thenReturn(false)
    }

    @Test
    fun givenWithContacts_whenContactInits_thenContacts() {
        `when`(contactViewModel.contacts).thenReturn(contactsLiveData)
        contactsLiveData.postValue(populateContactsList())

        onView(withId(R.id.recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText("FirstFriend"))))
    }

    private fun populateContactsList(): List<Friend> {
        val friend = Friend(10, "FirstFriend", "First friend name", null, "first@gmail.com", Relationship.ACCEPTED.name)
        val friend2 = friend.copy(
            friendId = 2,
            username = "Secondfriend",
            name = "Second Friend name",
            email = "second@gmail.com"
        )
        return listOf(friend, friend2)
    }

    class TestContactFragment : ContactFragment() {
        val navController = Mockito.mock(NavController::class.java)
        override fun navController() = navController
    }
}