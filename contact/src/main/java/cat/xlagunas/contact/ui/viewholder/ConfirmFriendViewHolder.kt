package cat.xlagunas.contact.ui.viewholder

import android.view.View
import android.widget.ImageButton
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cat.xlagunas.contact.R2
import cat.xlagunas.contact.ui.ContactListener

class ConfirmFriendViewHolder(view: View, contactListener: ContactListener) :
    FriendViewHolder(view, contactListener) {

    @BindView(R2.id.accept_friendship_button)
    lateinit var acceptFriendShipButton: ImageButton

    @BindView(R2.id.reject_friendship_button)
    lateinit var rejectFriendShipButton: ImageButton

    init {
        ButterKnife.bind(this, view)
    }

    @OnClick(R2.id.accept_friendship_button)
    fun acceptFriendRequest() {
        contactListener.onContactAccepted(friend)
    }

    @OnClick(R2.id.reject_friendship_button)
    fun rejectFriendshipRequest() {
        contactListener.onContactRejected(friend)
    }
}