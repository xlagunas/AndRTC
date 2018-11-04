package cat.xlagunas.viv.contact.viewholder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.contact.ContactListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

abstract class FriendViewHolder(view: View, internal val contactListener: ContactListener) :
    androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

    @BindView(R.id.contact_thumbnail)
    lateinit var profileImage: ImageView

    @BindView(R.id.contact_fullname)
    lateinit var fullName: TextView

    @BindView(R.id.contact_username)
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