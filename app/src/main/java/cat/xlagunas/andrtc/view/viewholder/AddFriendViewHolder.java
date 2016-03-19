package cat.xlagunas.andrtc.view.viewholder;

import android.view.View;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.util.OnFriendClickListener;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 17/03/16.
 */
public class AddFriendViewHolder extends FriendViewHolder {


    @Bind(R.id.contact_add_friendship)
    ImageView addFriendship;

    public AddFriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static void bind(final AddFriendViewHolder holder, final Friend friend, final OnFriendClickListener listener){
        FriendViewHolder.bind(holder, friend);

        if (listener != null){
            holder.addFriendship.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFriendRequested(friend);
                }
            });
        }
    }
}
