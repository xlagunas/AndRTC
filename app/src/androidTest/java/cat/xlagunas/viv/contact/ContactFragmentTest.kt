package cat.xlagunas.viv.contact

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import androidx.navigation.NavController
import cat.xlagunas.data.common.net.Relationship
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.ViewModelUtil
import cat.xlagunas.viv.contact.viewholder.ConfirmFriendViewHolder
import cat.xlagunas.viv.contact.viewholder.CurrentFriendViewHolder
import cat.xlagunas.viv.contact.viewholder.RequestFriendViewHolder
import cat.xlagunas.viv.push.PushTokenPresenter
import cat.xlagunas.viv.test.SingleFragmentActivity
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

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
        `when`(contactViewModel.contacts).thenReturn(contactsLiveData)
    }

    @Test
    fun givenWithContacts_whenContactInits_thenContacts() {
        contactsLiveData.postValue(populateContactsList())

        onView(withId(R.id.recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText("FirstFriend"))))

        onView(ViewMatchers.withId(R.id.recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RequestFriendViewHolder>(999))
    }

    @Test
    fun givenRequestedContact_whenAcceptingContact_thenAcceptCall() {
        val friend = Friend(0, "FirstFriend", "First friend name", null, "first@gmail.com", Relationship.PENDING.name)

        setupFriendRelationshipAndViewHolderType<ConfirmFriendViewHolder>(friend, R.id.accept_friendship_button, 0)

        verify(contactViewModel).acceptContactRequest(friend)
    }

    @Test
    fun givenRequestedContact_whenRejectedContact_thenRejectCall() {
        val friend = Friend(0, "FirstFriend", "First friend name", null, "first@gmail.com", Relationship.PENDING.name)
        setupFriendRelationshipAndViewHolderType<ConfirmFriendViewHolder>(friend, R.id.reject_friendship_button, 0)

        verify(contactViewModel).rejectContactRequest(friend)
    }

    @Test
    fun givenAcceptedContact_whenClicked_thenCall() {
        val friend = Friend(0, "FirstFriend", "First friend name", null, "first@gmail.com", Relationship.ACCEPTED.name)
        setupFriendRelationshipAndViewHolderType<CurrentFriendViewHolder>(friend, R.id.call_friend_button, 0)

        verify(contactViewModel).callFriend(friend)
    }

    private fun <VH : RecyclerView.ViewHolder> setupFriendRelationshipAndViewHolderType(
        friend: Friend,
        actionId: Int,
        holderPosition: Int
    ) {
        contactsLiveData.value = listOf(friend)

        onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<VH>(
                    holderPosition, MyViewAction.clickChildViewWithId(actionId)
                )
            )
    }

    private fun populateContactsList(): List<Friend> {
        val friend = Friend(0, "FirstFriend", "First friend name", null, "first@gmail.com", Relationship.REQUESTED.name)
        val friendList = ArrayList<Friend>(1000)
        friendList += friend

        for (i in 1..1000) {
            friendList += friend.copy(
                friendId = 1,
                username = "Secondfriend",
                name = "Second Friend name",
                email = "second@gmail.com"
            )
        }
        return friendList
    }

    class TestContactFragment : ContactFragment() {
        val navController = Mockito.mock(NavController::class.java)
        override fun navController() = navController
    }

    object MyViewAction {

        fun clickChildViewWithId(id: Int): ViewAction {
            return object : ViewAction {
                override fun getConstraints(): Matcher<View>? {
                    return null
                }

                override fun getDescription(): String {
                    return "Click on a child view with specified id."
                }

                override fun perform(uiController: UiController, view: View) {
                    val v = view.findViewById(id) as View
                    v.performClick()
                }
            }
        }
    }
}