package cat.xlagunas.contact.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.runner.AndroidJUnit4
import cat.xlagunas.contact.R
import cat.xlagunas.contact.ui.viewholder.ConfirmFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.CurrentFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.RequestFriendViewHolder
import cat.xlagunas.core.di.ViewModelUtil
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.push.PushTokenPresenter
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class ContactFragmentTest {

    private val contactViewModel = mock(ContactViewModel::class.java)
    private val pushTokenPresenter = mock(PushTokenPresenter::class.java)
    private val contactsLiveData = MutableLiveData<List<Friend>>()

    private lateinit var testContactFactory: TestContactFragmentFactory

    @Before
    fun setUp() {
        testContactFactory = TestContactFragmentFactory(ViewModelUtil.createFor(contactViewModel))

        `when`(pushTokenPresenter.isPushTokenRegistered()).thenReturn(false)
        `when`(contactViewModel.contacts).thenReturn(contactsLiveData)

        launchFragmentInContainer<TestContactFragment>(Bundle(), R.style.AppTheme_NoActionBar, testContactFactory)
    }

    @Test
    fun givenWithContacts_whenContactInits_thenContacts() {
        contactsLiveData.postValue(populateContactsList())

        onView(withId(R.id.recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText("FirstFriend"))))

        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.scrollToPosition<RequestFriendViewHolder>(999))
    }

    @Test
    fun givenRequestedContact_whenAcceptingContact_thenAcceptCall() {
        val friend = Friend(
            0,
            "FirstFriend",
            "First friend name",
            null,
            "first@gmail.com",
            cat.xlagunas.core.data.net.Relationship.PENDING.name
        )

        setupFriendRelationshipAndViewHolderType<ConfirmFriendViewHolder>(
            friend,
            R.id.accept_friendship_button,
            0
        )

        verify(contactViewModel).acceptContactRequest(friend)
    }

    @Test
    fun givenRequestedContact_whenRejectedContact_thenRejectCall() {
        val friend = Friend(
            0,
            "FirstFriend",
            "First friend name",
            null,
            "first@gmail.com",
            cat.xlagunas.core.data.net.Relationship.PENDING.name
        )
        setupFriendRelationshipAndViewHolderType<ConfirmFriendViewHolder>(
            friend,
            R.id.reject_friendship_button,
            0
        )

        verify(contactViewModel).rejectContactRequest(friend)
    }

    @Test
    fun givenAcceptedContact_whenClicked_thenCall() {
        val friend = Friend(
            0,
            "FirstFriend",
            "First friend name",
            null,
            "first@gmail.com",
            cat.xlagunas.core.data.net.Relationship.ACCEPTED.name
        )
        setupFriendRelationshipAndViewHolderType<CurrentFriendViewHolder>(
            friend,
            R.id.call_friend_button,
            0
        )

        verify(contactViewModel).observeCall(listOf(friend))
    }

    private fun <VH : androidx.recyclerview.widget.RecyclerView.ViewHolder> setupFriendRelationshipAndViewHolderType(
        friend: Friend,
        actionId: Int,
        holderPosition: Int
    ) {
        contactsLiveData.postValue(listOf(friend))

        onView(withId(R.id.recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition(holderPosition, MyViewAction.clickChildViewWithId(actionId)))
    }

    private fun populateContactsList(): List<Friend> {
        val friend = Friend(
            0,
            "FirstFriend",
            "First friend name",
            null,
            "first@gmail.com",
            cat.xlagunas.core.data.net.Relationship.REQUESTED.name
        )
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

        override fun inject() {}
    }

    class TestContactFragmentFactory(
        private val contactViewModelFactory: ViewModelProvider.Factory
    ) : FragmentFactory() {
        override fun instantiate(
            classLoader: ClassLoader,
            className: String,
            args: Bundle?
        ): Fragment {
            return TestContactFragment()
                .apply {
                    viewModelFactory = contactViewModelFactory
                }
        }
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