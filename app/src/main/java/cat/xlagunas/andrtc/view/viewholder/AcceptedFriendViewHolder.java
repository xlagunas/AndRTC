package cat.xlagunas.andrtc.view.viewholder;

import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.util.OnFriendClickListener;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 22/03/16.
 */
public class AcceptedFriendViewHolder extends FriendViewHolder {

    @BindView(R.id.contact_call)
    ImageButton callImageView;

    public AcceptedFriendViewHolder(View itemView) {
        super(itemView);
    }

    public static void bind(final AcceptedFriendViewHolder holder, final Friend friend, final OnFriendClickListener listener) {
        FriendViewHolder.bind(holder, friend);
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(holder.getAdapterPosition(), friend);
                }
            });
        }
    }
}
