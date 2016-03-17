package cat.xlagunas.andrtc.view.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.util.OnClickListener;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 17/03/16.
 */
public class NewContactViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.contact_username)
    TextView username;
    @Bind(R.id.contact_fullname)
    TextView fullName;
    @Bind(R.id.contact_thumbnail)
    ImageView thumbnail;
    @Bind(R.id.contact_add_friendship)
    ImageView addFriendship;

    public NewContactViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static void bind(final NewContactViewHolder holder, final Friend friend, final OnClickListener listener){
        holder.username.setText(friend.getUsername());
        holder.fullName.setText(friend.getName() +" " +friend.getSurname() + " "+ friend.getLastSurname());
        //TODO add thumbnail loading image
        if (listener != null){
            holder.addFriendship.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(holder.getAdapterPosition(), friend);
                }
            });
        }
    }
}
