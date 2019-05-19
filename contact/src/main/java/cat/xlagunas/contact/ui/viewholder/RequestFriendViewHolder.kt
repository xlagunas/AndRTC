package cat.xlagunas.contact.ui.viewholder

import android.view.View
import android.widget.ImageButton
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cat.xlagunas.contact.R2
import cat.xlagunas.contact.ui.ContactListener

class RequestFriendViewHolder(view: View, contactListener: ContactListener) :
    FriendViewHolder(view, contactListener) {

    @BindView(R2.id.add_friend_button)
    lateinit var addFriendButton: ImageButton

    init {
        ButterKnife.bind(this, view)
    }

    @OnClick(R2.id.add_friend_button)
    fun addFriend() {
        contactListener.onContactRequested(friend)
    }
}