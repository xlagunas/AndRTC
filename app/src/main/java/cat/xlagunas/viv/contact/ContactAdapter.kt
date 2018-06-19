package cat.xlagunas.viv.contact

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import cat.xlagunas.viv.landing.MainActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ContactAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var items = emptyList<Friend>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = LayoutInflater.from(parent.context).inflate(R.layout.row_contact, parent, false)
        return FriendViewHolder(holder)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FriendViewHolder).bind(items[position])
    }


    fun updateAdapter(elements: List<Friend>) {
        items = elements
        notifyDataSetChanged()
    }
}


class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    @BindView(R.id.contact_thumbnail)
    lateinit var profileImage: ImageView

    @BindView(R.id.contact_fullname)
    lateinit var fullName: TextView

    @BindView(R.id.contact_username)
    lateinit var username: TextView

    @BindView(R.id.add_friend_button)
    lateinit var addFriend: Button

    private val friendshipViewModel: FriendshipViewModel

    init {
        ButterKnife.bind(this, view)
        val viewModelFactory = ((view.context as MainActivity).application as VivApplication).viewModelFactory
        friendshipViewModel = ViewModelProviders.of((view.context as MainActivity), viewModelFactory).get(FriendshipViewModel::class.java)
    }

    fun bind(friend: Friend) {

        fullName.text = friend.name
        username.text = friend.username

        Glide.with(itemView)
                .load(friend.image)
                .apply(RequestOptions.noAnimation())
                .apply(RequestOptions.placeholderOf(R.drawable.profile_placeholder))
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage)

        addFriend.setOnClickListener { friendshipViewModel.addContact(friend.friendId) }
    }

}