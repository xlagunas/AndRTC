package cat.xlagunas.contact.ui.viewholder

import android.view.View
import cat.xlagunas.contact.databinding.RowConfirmContactBinding
import cat.xlagunas.contact.ui.ContactListener
import cat.xlagunas.core.domain.entity.Friend

class ConfirmFriendViewHolder(view: View, contactListener: ContactListener) :
    FriendViewHolder(view, contactListener) {

    private var _bind: RowConfirmContactBinding? = null
    private val bind get() = _bind!!

    init {
        _bind = RowConfirmContactBinding.bind(view)
    }

    override fun bind(friend: Friend) {
        setupView(bind.contactBase, friend)
        bind.acceptFriendshipButton.setOnClickListener { contactListener.onContactAccepted(friend) }
        bind.rejectFriendshipButton.setOnClickListener { contactListener.onContactRejected(friend) }
    }
}