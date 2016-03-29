package cat.xlagunas.andrtc.view.viewholder;

import android.view.View;
import android.widget.ImageView;

import butterknife.Bind;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.util.OnFriendClickListener;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public class PendingFriendViewHolder extends FriendViewHolder {
    @Bind(R.id.contact_accept_friendship)
    ImageView acceptContact;

    @Bind(R.id.contact_reject_friendship)
    ImageView rejectContact;

    public PendingFriendViewHolder(View itemView) {
        super(itemView);
    }

    public static void bind(final PendingFriendViewHolder holder, final Friend friend, final OnFriendClickListener listener) {
        FriendViewHolder.bind(holder, friend);

        if (listener != null) {
            holder.acceptContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFriendAccepted(friend);
                }
            });

            holder.rejectContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFriendRejected(friend);
                }
            });
        }
    }
}
