package cat.xlagunas.viv.contact

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cat.xlagunas.call.Call
import cat.xlagunas.contact.R
import cat.xlagunas.contact.data.Relationship.ACCEPTED
import cat.xlagunas.contact.data.Relationship.PENDING
import cat.xlagunas.contact.data.Relationship.REQUESTED
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.contact.ui.ContactFragment
import cat.xlagunas.contact.ui.ContactViewModel
import cat.xlagunas.contact.ui.viewholder.ConfirmFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.CurrentFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.RequestFriendViewHolder
import cat.xlagunas.test.utils.ViewModelUtil
import cat.xlagunas.viv.commons.TestApplication
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
    private val contactsLiveData = MutableLiveData<Result<List<Friend>>>()

    @Before
    fun setUp() {
        val application =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as TestApplication
        application.setViewModelProviderFactory(ViewModelUtil.createFor(contactViewModel))
        `when`(contactViewModel.contacts).thenReturn(contactsLiveData)

        launchFragmentInContainer<ContactFragment>(Bundle(), R.style.AppTheme_NoActionBar)
    }

    @Test
    fun givenWithContacts_whenContactInits_thenContacts() {
        contactsLiveData.postValue(Result.success(populateContactsList()))

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
            PENDING.name
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
            PENDING.name
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
            ACCEPTED.name
        )
        Mockito.doAnswer {
            MutableLiveData<Call>().apply {
                value =
                    Call("123456")
            }
        }
            .`when`(contactViewModel).createCall(listOf(friend))
        setupFriendRelationshipAndViewHolderType<CurrentFriendViewHolder>(
            friend,
            R.id.call_friend_button,
            0
        )

        verify(contactViewModel).createCall(listOf(friend))
    }

    private fun <VH : ViewHolder> setupFriendRelationshipAndViewHolderType(
        friend: Friend,
        actionId: Int,
        holderPosition: Int
    ) {
        contactsLiveData.postValue(Result.success(listOf(friend)))

        onView(withId(R.id.recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(
                    holderPosition,
                    MyViewAction.clickChildViewWithId(
                        actionId
                    )
                )
            )
    }

    private fun populateContactsList(): List<Friend> {
        val friend = Friend(
            0,
            "FirstFriend",
            "First friend name",
            null,
            "first@gmail.com",
            REQUESTED.name
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
