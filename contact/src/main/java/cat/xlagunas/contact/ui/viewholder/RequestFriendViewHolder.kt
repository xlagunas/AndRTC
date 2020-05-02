package cat.xlagunas.contact.ui.viewholder

import android.view.View
import cat.xlagunas.contact.databinding.RowRequestContactBinding
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.contact.ui.ContactListener

class RequestFriendViewHolder(view: View, contactListener: ContactListener) :
    FriendViewHolder(view, contactListener) {

    private var _bind: RowRequestContactBinding? = null
    private val bind get() = _bind!!

    init {
        _bind = RowRequestContactBinding.bind(view)
    }

    override fun bind(friend: Friend) {
        bind.addFriendButton.setOnClickListener {
            contactListener.onContactRequested(friend)
        }
    }
}
