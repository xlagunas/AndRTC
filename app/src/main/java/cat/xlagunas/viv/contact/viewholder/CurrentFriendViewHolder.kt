package cat.xlagunas.viv.contact.viewholder

import android.view.View
import butterknife.ButterKnife
import butterknife.OnClick
import cat.xlagunas.viv.R
import cat.xlagunas.viv.contact.ContactListener

class CurrentFriendViewHolder(view: View, contactListener: ContactListener) : FriendViewHolder(view, contactListener) {

    init {
        ButterKnife.bind(this, view)
    }

    @OnClick(R.id.call_friend_button)
    fun callFriend() {
        contactListener.onContactCalled(friend)
    }
}