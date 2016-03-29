package cat.xlagunas.andrtc.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.util.OnFriendClickListener;
import cat.xlagunas.andrtc.view.viewholder.AcceptedFriendViewHolder;
import cat.xlagunas.andrtc.view.viewholder.FriendViewHolder;
import cat.xlagunas.andrtc.view.viewholder.AddFriendViewHolder;
import cat.xlagunas.andrtc.view.viewholder.PendingFriendViewHolder;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 17/03/16.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private List<Friend> elements;
    private OnFriendClickListener listener;
    private LayoutInflater inflater;

    public FriendAdapter(List<Friend> elements) {
        this.elements = elements;
    }

    public void setOnClickListener(OnFriendClickListener listener){
        this.listener = listener;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        switch (viewType) {
            case 0:
                return new AddFriendViewHolder(inflater.inflate(R.layout.item_friend_add, parent, false));
            case Friend.PENDING:
                return new PendingFriendViewHolder(inflater.inflate(R.layout.item_friend_pending, parent, false));
            case Friend.ACCEPTED:
                return new AcceptedFriendViewHolder(inflater.inflate(R.layout.item_friend_accepted, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        Friend friend = elements.get(position);
        switch (getItemViewType(position)){
            case 0:
                AddFriendViewHolder.bind((AddFriendViewHolder) holder, friend, listener);
                break;
            case Friend.PENDING:
                PendingFriendViewHolder.bind((PendingFriendViewHolder) holder, friend, listener);
                break;
            case Friend.ACCEPTED:
                AcceptedFriendViewHolder.bind((AcceptedFriendViewHolder) holder, friend, listener);
        }
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    @Override
    public int getItemViewType(int position) {
        return elements.get(position).getFriendState();
    }

    public void clear(){
        elements.clear();
    }

    public void add(Friend friend){
        elements.add(friend);
    }

    public void remove(Friend friend){
        elements.remove(friend);
    }
}
