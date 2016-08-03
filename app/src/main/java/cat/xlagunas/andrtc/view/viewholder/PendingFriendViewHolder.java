package cat.xlagunas.andrtc.view.viewholder;

import android.view.View;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 1/8/16.
 */
public class PendingFriendViewHolder extends FriendViewHolder {

    public PendingFriendViewHolder(View itemView) {
        super(itemView);
    }

    public static void bind(final FriendViewHolder holder, final Friend friend) {
        FriendViewHolder.bind(holder, friend);
    }
}
