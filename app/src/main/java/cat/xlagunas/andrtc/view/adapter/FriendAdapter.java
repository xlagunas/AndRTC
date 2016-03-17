package cat.xlagunas.andrtc.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.util.OnClickListener;
import cat.xlagunas.andrtc.view.viewholders.NewContactViewHolder;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 17/03/16.
 */
public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Friend> elements;
    private OnClickListener listener;
    private LayoutInflater inflater;

    public FriendAdapter(List<Friend> elements) {
        this.elements = elements;
    }

    public void setOnClickListener(OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }

        switch (viewType) {
                case 0:
                    return new NewContactViewHolder(inflater.inflate(R.layout.item_friend, parent, false));
            }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Friend friend = elements.get(position);
        switch (getItemViewType(position)){
            case 0:
                NewContactViewHolder.bind((NewContactViewHolder) holder, friend, listener);
                break;
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
