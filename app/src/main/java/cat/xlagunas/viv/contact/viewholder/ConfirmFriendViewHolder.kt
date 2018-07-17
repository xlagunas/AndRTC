package cat.xlagunas.viv.contact.viewholder

import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import cat.xlagunas.viv.R
import cat.xlagunas.viv.contact.ContactListener

class ConfirmFriendViewHolder(view: View, contactListener: ContactListener) : FriendViewHolder(view, contactListener) {


    init {
        ButterKnife.bind(this, view)
    }

    @OnClick(R.id.add_friend_button)
    fun acceptFriendRequest() {
        contactListener.onContactAccepted(friend)
    }

    @OnClick(R.id.reject_friendship_button)
    fun rejectFriendshipRequest() {
        contactListener.onContactRejected(friend)
    }
}