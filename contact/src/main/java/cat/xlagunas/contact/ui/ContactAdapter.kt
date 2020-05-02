package cat.xlagunas.contact.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cat.xlagunas.contact.R
import cat.xlagunas.contact.data.Relationship.ACCEPTED
import cat.xlagunas.contact.data.Relationship.NONE
import cat.xlagunas.contact.data.Relationship.PENDING
import cat.xlagunas.contact.data.Relationship.REQUESTED
import cat.xlagunas.contact.databinding.RowConfirmContactBinding
import cat.xlagunas.contact.databinding.RowContactBinding
import cat.xlagunas.contact.databinding.RowPendingContactBinding
import cat.xlagunas.contact.databinding.RowRequestContactBinding
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.contact.ui.viewholder.ConfirmFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.CurrentFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.FriendViewHolder
import cat.xlagunas.contact.ui.viewholder.PendingFriendViewHolder
import cat.xlagunas.contact.ui.viewholder.RequestFriendViewHolder

class ContactAdapter constructor(private val contactListener: ContactListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = emptyList<Friend>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.row_request_contact -> RequestFriendViewHolder(
                RowRequestContactBinding.inflate(layoutInflater, parent, false).root,
                contactListener
            )
            R.layout.row_pending_contact -> PendingFriendViewHolder(
                RowPendingContactBinding.inflate(layoutInflater, parent, false).root,
                contactListener
            )
            R.layout.row_confirm_contact -> ConfirmFriendViewHolder(
                RowConfirmContactBinding.inflate(layoutInflater, parent, false).root,
                contactListener
            )
            R.layout.row_contact -> CurrentFriendViewHolder(
                RowContactBinding.inflate(layoutInflater, parent, false).root,
                contactListener
            )
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
