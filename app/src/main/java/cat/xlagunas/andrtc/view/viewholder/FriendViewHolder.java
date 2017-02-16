package cat.xlagunas.andrtc.view.viewholder;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.adapter.FriendAdapter;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public abstract class FriendViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.contact_username)
    TextView username;
    @BindView(R.id.contact_fullname)
    TextView fullName;
    @BindView(R.id.contact_thumbnail)
    ImageView thumbnail;

    public FriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(final Friend friend) {
        this.username.setText(friend.getUsername());
        this.fullName.setText(this.itemView.getContext()
                .getString(R.string.friend_item_name, friend.getName(), friend.getSurname(), friend.getLastSurname()));

        Glide.with(this.itemView.getContext())
                .load(Uri.parse(friend.getThumbnail()))
                .placeholder(R.drawable.user_profile)
                .dontAnimate()
                .into(this.thumbnail);
    }

    protected FriendAdapter getFriendAdapter() {
        return (FriendAdapter) ((RecyclerView) itemView.getParent()).getAdapter();
    }
}
