package cat.xlagunas.andrtc.view.viewholder;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.auto.factory.AutoFactory;

import butterknife.BindView;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.activity.CallRequestActivity;
import cat.xlagunas.andrtc.domain.Friend;

/**
 * Created by xlagunas on 22/03/16.
 */
@AutoFactory(implementing = FriendViewHolderFactory.class)
public class AcceptedFriendViewHolder extends FriendViewHolder {

    @BindView(R.id.contact_call)
    ImageButton callImageView;

    public AcceptedFriendViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(getLayoutResource((RecyclerView) parent), parent, false));
        itemView.setOnClickListener(view -> startCallerActivity());
    }

    private void startCallerActivity() {
        Friend friend = getFriendAdapter().getItem(getAdapterPosition());
        itemView.getContext().startActivity(CallRequestActivity.makeCallerIntent(itemView.getContext(), friend.getId()));
    }

    private static int getLayoutResource(RecyclerView parent) {
        int layoutResource;
        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            layoutResource = R.layout.item_friend_accepted_grid;
        } else {
            layoutResource = R.layout.item_friend_accepted;
        }
        return layoutResource;
    }
}
