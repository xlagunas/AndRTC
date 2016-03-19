package cat.xlagunas.andrtc.view.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public abstract class FriendViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.contact_username)
    TextView username;
    @Bind(R.id.contact_fullname)
    TextView fullName;
    @Bind(R.id.contact_thumbnail)
    ImageView thumbnail;

    public FriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    protected static void bind(final FriendViewHolder holder, final Friend friend) {
        holder.username.setText(friend.getUsername());
        holder.fullName.setText(friend.getName() +" " +friend.getSurname() + " "+ friend.getLastSurname());
        //TODO ADD THUMBNAIL
//        holder.thumbnail
    }
}
