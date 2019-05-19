package cat.xlagunas.contact.ui.viewholder

import android.view.View
import android.widget.ImageButton
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cat.xlagunas.contact.R2
import cat.xlagunas.contact.ui.ContactListener

class CurrentFriendViewHolder(view: View, contactListener: ContactListener) : FriendViewHolder(view, contactListener) {

    @BindView(R2.id.call_friend_button)
    lateinit var callFriendButton: ImageButton

    init {
        ButterKnife.bind(this, view)
    }

    @OnClick(R2.id.call_friend_button)
    fun callFriend() {
        contactListener.onContactCalled(friend)
    }
}