package cat.xlagunas.contact.ui.viewholder

import android.view.View
import cat.xlagunas.contact.databinding.RowPendingContactBinding
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.contact.ui.ContactListener

class PendingFriendViewHolder(view: View, contactListener: ContactListener) :
    FriendViewHolder(view, contactListener) {

    private var _bind: RowPendingContactBinding? = null
    private val bind get() = _bind!!

    init {
        _bind = RowPendingContactBinding.bind(view)
    }

    override fun bind(friend: Friend) {
        setupView(bind.contactBase, friend)
    }
}
