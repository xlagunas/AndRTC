package cat.xlagunas.viv.contact

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cat.xlagunas.data.common.net.Relationship
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.contact.viewholder.ConfirmFriendViewHolder
import cat.xlagunas.viv.contact.viewholder.CurrentFriendViewHolder
import cat.xlagunas.viv.contact.viewholder.FriendViewHolder
import cat.xlagunas.viv.contact.viewholder.PendingFriendViewHolder
import cat.xlagunas.viv.contact.viewholder.RequestFriendViewHolder

class ContactAdapter constructor(private val contactListener: ContactListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = emptyList<Friend>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FriendViewHolder).bind(items[position])
    }

    fun updateAdapter(elements: List<Friend>) {
        items = elements
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].relationshipStatus) {
            Relationship.NONE.name -> R.layout.row_request_contact
            Relationship.REQUESTED.name -> R.layout.row_pending_contact
            Relationship.ACCEPTED.name -> R.layout.row_contact
            Relationship.PENDING.name -> R.layout.row_confirm_contact
            else -> throw IllegalStateException("This should never be called")
        }
    }
}
