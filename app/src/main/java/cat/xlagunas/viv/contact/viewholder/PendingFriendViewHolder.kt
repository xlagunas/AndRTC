package cat.xlagunas.viv.contact.viewholder

import android.view.View
import butterknife.ButterKnife
import cat.xlagunas.viv.contact.ContactListener

class PendingFriendViewHolder(view: View, contactListener: ContactListener) : FriendViewHolder(view, contactListener) {

    init {
        ButterKnife.bind(this, view)
    }
}