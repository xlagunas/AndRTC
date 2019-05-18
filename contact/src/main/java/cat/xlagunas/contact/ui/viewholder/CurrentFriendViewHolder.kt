package cat.xlagunas.viv.contact.viewholder

import android.view.View
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cat.xlagunas.contact.R2
import cat.xlagunas.contact.ui.ContactListener

class CurrentFriendViewHolder(view: View, contactListener: ContactListener) :
    FriendViewHolder(view, contactListener) {

    @BindView(R2.id.call_friend_button)
    lateinit var callFriendButton: ImageView

    init {
        ButterKnife.bind(this, view)
    }

    @OnClick(R.id.call_friend_button)
    fun callFriend() {
        contactListener.onContactCalled(friend)
    }
}