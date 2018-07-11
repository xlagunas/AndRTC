package cat.xlagunas.viv.contact.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

abstract class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.contact_thumbnail)
    lateinit var profileImage: ImageView

    @BindView(R.id.contact_fullname)
    lateinit var fullName: TextView

    @BindView(R.id.contact_username)
    lateinit var username: TextView

    open fun bind(friend: Friend) {
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