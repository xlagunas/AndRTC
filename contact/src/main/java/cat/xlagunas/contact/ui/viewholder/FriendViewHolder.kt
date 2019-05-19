package cat.xlagunas.contact.ui.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import cat.xlagunas.contact.R
import cat.xlagunas.contact.R2
import cat.xlagunas.contact.ui.ContactListener
import cat.xlagunas.core.domain.entity.Friend
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

abstract class FriendViewHolder(view: View, internal val contactListener: ContactListener) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    @BindView(R2.id.contact_thumbnail)
    lateinit var profileImage: ImageView

    @BindView(R2.id.contact_fullname)
    lateinit var fullName: TextView

    @BindView(R2.id.contact_username)
    lateinit var username: TextView

    internal lateinit var friend: Friend

    open fun bind(friend: Friend) {
        this.friend = friend
        fullName.text = friend.name
        username.text = friend.username

        Glide.with(itemView)
            .load(friend.image)
            .apply(RequestOptions.noAnimation())
            .apply(RequestOptions.placeholderOf(R.drawable.profile_placeholder))
            .apply(RequestOptions.circleCropTransform())
            .into(profileImage)
    }
}