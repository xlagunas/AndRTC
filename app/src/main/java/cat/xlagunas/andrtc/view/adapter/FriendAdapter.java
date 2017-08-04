package cat.xlagunas.andrtc.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.viewholder.FriendViewHolder;
import cat.xlagunas.andrtc.view.viewholder.FriendViewHolderFactory;
import cat.xlagunas.andrtc.domain.Friend;

/**
 * Created by xlagunas on 17/03/16.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendViewHolder> {

    private Map<Integer, FriendViewHolderFactory> viewHolderFactories;
    private List<Friend> elements;

    @Inject
    public FriendAdapter(Map<Integer, FriendViewHolderFactory> friendViewHolderFactory) {
        elements = new ArrayList<>();
        this.viewHolderFactories = friendViewHolderFactory;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return viewHolderFactories.get(viewType).createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    @Override
    public int getItemViewType(int position) {
        return elements.get(position).getFriendState();
    }

    public void clear() {
        elements.clear();
    }

    public void addAll(List<Friend> friends) {
        elements.addAll(friends);
    }

    public void add(Friend friend) {
        elements.add(friend);
    }

    public void remove(Friend friend) {
        elements.remove(friend);
    }

    public Friend getItem(int position) {
        return elements.get(position);
    }
}
