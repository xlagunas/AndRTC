package cat.xlagunas.andrtc.view.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import butterknife.BindView;
import cat.xlagunas.andrtc.R;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.interactor.RequestNewFriendshipUseCase;

/**
 * Created by xlagunas on 17/03/16.
 */
@AutoFactory(implementing = FriendViewHolderFactory.class)
public class AddFriendViewHolder extends FriendViewHolder {

    @BindView(R.id.contact_add_friendship)
    ImageView addFriendship;

    private final RequestNewFriendshipUseCase requestNewFriendshipUseCase;

    public AddFriendViewHolder(ViewGroup parent, @Provided RequestNewFriendshipUseCase requestNewFriendshipUseCase) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_add, parent, false));
        this.requestNewFriendshipUseCase = requestNewFriendshipUseCase;
        itemView.setOnClickListener(v -> requesteNewFriendship());
    }

    private void requesteNewFriendship() {
        Friend friend = getFriendAdapter().getItem(getAdapterPosition());
        requestNewFriendshipUseCase.setNewContactId(friend.getId());
        requestNewFriendshipUseCase.execute(new DefaultSubscriber<>());
    }

    public void bind(Friend friend) {
        super.bind(friend);
    }
}
