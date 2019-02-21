package cat.xlagunas.viv.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.contact.viewholder.ConfirmFriendViewHolder
import cat.xlagunas.viv.contact.viewholder.CurrentFriendViewHolder
import cat.xlagunas.viv.contact.viewholder.FriendViewHolder
import cat.xlagunas.viv.contact.viewholder.PendingFriendViewHolder
import cat.xlagunas.viv.contact.viewholder.RequestFriendViewHolder

class ContactAdapter constructor(private val contactListener: ContactListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    private var items = emptyList<Friend>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.row_request_contact -> RequestFriendViewHolder(holder, contactListener)
            R.layout.row_pending_contact -> PendingFriendViewHolder(holder, contactListener)
            R.layout.row_confirm_contact -> ConfirmFriendViewHolder(holder, contactListener)
            R.layout.row_contact -> CurrentFriendViewHolder(holder, contactListener)
            else -> throw IllegalStateException("This should never be called")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(
        holder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as FriendViewHolder).bind(items[position])
    }

    fun updateAdapter(elements: List<Friend>) {
        items = elements
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].relationshipStatus) {
            cat.xlagunas.core.data.net.Relationship.NONE.name -> R.layout.row_request_contact
            cat.xlagunas.core.data.net.Relationship.REQUESTED.name -> R.layout.row_pending_contact
            cat.xlagunas.core.data.net.Relationship.ACCEPTED.name -> R.layout.row_contact
            cat.xlagunas.core.data.net.Relationship.PENDING.name -> R.layout.row_confirm_contact
            else -> throw IllegalStateException("This should never be called")
        }
    }
}
