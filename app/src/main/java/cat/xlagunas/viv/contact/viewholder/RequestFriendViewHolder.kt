package cat.xlagunas.viv.contact.viewholder

import android.arch.lifecycle.ViewModelProviders
import android.view.View
import android.widget.ImageButton
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import cat.xlagunas.viv.contact.FriendshipViewModel
import cat.xlagunas.viv.landing.MainActivity

class RequestFriendViewHolder(view: View) : FriendViewHolder(view) {

    @BindView(R.id.add_friend_button)
    lateinit var addFriend: ImageButton

    private val friendshipViewModel: FriendshipViewModel

    init {
        ButterKnife.bind(this, view)
        val viewModelFactory = ((view.context as MainActivity).application as VivApplication).viewModelFactory
        friendshipViewModel = ViewModelProviders.of((view.context as MainActivity), viewModelFactory).get(FriendshipViewModel::class.java)
    }

    override fun bind(friend: Friend) {
        super.bind(friend)
        addFriend.setOnClickListener { friendshipViewModel.addContact(friend) }
    }

}