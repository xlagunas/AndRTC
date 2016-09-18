package cat.xlagunas.andrtc.view.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.auto.factory.AutoFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.util.OnFriendClickListener;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 17/03/16.
 */
@AutoFactory(implementing = FriendViewHolderFactory.class)
public class AddFriendViewHolder extends FriendViewHolder {

    @BindView(R.id.contact_add_friendship)
    ImageView addFriendship;

    public AddFriendViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_add, parent, false));
    }

    public void bind(Friend friend) {
        super.bind(friend);
    }
}
