package cat.xlagunas.contact.ui.viewholder

import android.view.View
import cat.xlagunas.contact.databinding.RowContactBinding
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.contact.ui.ContactListener

class CurrentFriendViewHolder(view: View, contactListener: ContactListener) :
    FriendViewHolder(view, contactListener) {

    private var _bind: RowContactBinding? = null
    private val bind get() = _bind!!

    init {
        _bind = RowContactBinding.bind(view)
    }

    override fun bind(friend: Friend) {
        setupView(bind.contactBase, friend)
        bind.callFriendButton.setOnClickListener { contactListener.onContactCalled(friend) }
    }
}
