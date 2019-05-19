package cat.xlagunas.contact.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cat.xlagunas.contact.R
import cat.xlagunas.contact.ui.viewholder.ConfirmFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.CurrentFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.FriendViewHolder
import cat.xlagunas.contact.ui.viewholder.PendingFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.RequestFriendViewHolder
import cat.xlagunas.core.data.net.Relationship.ACCEPTED
import cat.xlagunas.core.data.net.Relationship.NONE
import cat.xlagunas.core.data.net.Relationship.PENDING
import cat.xlagunas.core.data.net.Relationship.REQUESTED
import cat.xlagunas.core.domain.entity.Friend

class ContactAdapter constructor(private val contactListener: ContactListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = emptyList<Friend>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.row_request_contact -> RequestFriendViewHolder(
                holder,
                contactListener
            )
            R.layout.row_pending_contact -> PendingFriendViewHolder(
                holder,
                contactListener
            )
            R.layout.row_confirm_contact -> ConfirmFriendViewHolder(
                holder,
                contactListener
            )
            R.layout.row_contact -> CurrentFriendViewHolder(holder, contactListener)
            else -> throw IllegalStateException("This should never be called")
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
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
            NONE.name -> R.layout.row_request_contact
            REQUESTED.name -> R.layout.row_pending_contact
            ACCEPTED.name -> R.layout.row_contact
            PENDING.name -> R.layout.row_confirm_contact
            else -> throw IllegalStateException("This should never be called")
        }
    }
}
