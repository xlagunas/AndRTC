package cat.xlagunas.contact.ui.viewholder

import android.view.View
import butterknife.ButterKnife
import cat.xlagunas.contact.ui.ContactListener

class PendingFriendViewHolder(view: View, contactListener: ContactListener) : FriendViewHolder(view, contactListener) {

    init {
        ButterKnife.bind(this, view)
    }
}