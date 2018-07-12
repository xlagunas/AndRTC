package cat.xlagunas.viv.contact.viewholder

import android.arch.lifecycle.ViewModelProviders
import android.view.View
import android.widget.ImageButton
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import cat.xlagunas.viv.landing.MainActivity

class ConfirmFriendViewHolder(view: View) : FriendViewHolder(view) {

    @BindView(R.id.accept_friendship_button)
    lateinit var acceptFriend: ImageButton

    @BindView(R.id.reject_friendship_button)
    lateinit var rejectFriend: ImageButton

    private val friendshipViewModel: FriendshipViewModel

    init {
        ButterKnife.bind(this, view)
        val viewModelFactory = ((view.context as MainActivity).application as VivApplication).viewModelFactory
        friendshipViewModel =
            ViewModelProviders.of((view.context as MainActivity), viewModelFactory).get(FriendshipViewModel::class.java)
    }

    override fun bind(friend: Friend) {
        super.bind(friend)
        acceptFriend.setOnClickListener { friendshipViewModel.acceptContactRequest(friend) }
        rejectFriend.setOnClickListener { friendshipViewModel.rejectContactRequest(friend) }
    }
}