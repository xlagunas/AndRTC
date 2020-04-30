package cat.xlagunas.contact.ui.viewholder

import android.view.View
import cat.xlagunas.contact.R
import cat.xlagunas.contact.databinding.RowBaseBinding
import cat.xlagunas.contact.ui.ContactListener
import cat.xlagunas.core.domain.entity.Friend
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

abstract class FriendViewHolder(view: View, internal val contactListener: ContactListener) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    internal fun setupView(binding: RowBaseBinding, friend: Friend) {
        binding.contactFullname.text = friend.name
        binding.contactUsername.text = friend.username

        Glide.with(itemView)
            .load(friend.image)
            .apply(RequestOptions.noAnimation())
            .apply(RequestOptions.placeholderOf(R.drawable.profile_placeholder))
            .apply(RequestOptions.circleCropTransform())
            .into(binding.contactThumbnail)
    }

    open fun bind(friend: Friend) {}
}