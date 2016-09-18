package cat.xlagunas.andrtc.view.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.auto.factory.AutoFactory;

import butterknife.BindView;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.util.OnFriendClickListener;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 22/03/16.
 */
@AutoFactory(implementing = FriendViewHolderFactory.class)
public class AcceptedFriendViewHolder extends FriendViewHolder {

    @BindView(R.id.contact_call)
    ImageButton callImageView;

    public AcceptedFriendViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_accepted, parent, false));
    }
}
