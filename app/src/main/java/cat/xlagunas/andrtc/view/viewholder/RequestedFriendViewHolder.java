package cat.xlagunas.andrtc.view.viewholder;

import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.util.OnFriendClickListener;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public class RequestedFriendViewHolder extends FriendViewHolder {
    @BindView(R.id.contact_accept_friendship)
    ImageButton acceptContact;

    @BindView(R.id.contact_reject_friendship)
    ImageButton rejectContact;

    public RequestedFriendViewHolder(View itemView) {
        super(itemView);
    }

    public static void bind(final RequestedFriendViewHolder holder, final Friend friend, final OnFriendClickListener listener) {
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
