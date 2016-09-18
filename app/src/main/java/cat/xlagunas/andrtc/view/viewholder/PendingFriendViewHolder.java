package cat.xlagunas.andrtc.view.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.auto.factory.AutoFactory;

import cat.xlagunas.andrtc.R;

/**
 * Created by xlagunas on 1/8/16.
 */

@AutoFactory(implementing = FriendViewHolderFactory.class)
public class PendingFriendViewHolder extends FriendViewHolder {

    public PendingFriendViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_pending, parent, false));
    }
}
