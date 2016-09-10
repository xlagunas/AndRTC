package cat.xlagunas.andrtc.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.adapter.FriendAdapter;
import cat.xlagunas.andrtc.view.util.OnFriendClickListener;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public abstract class BaseContactFragment extends BaseFragment {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    FriendAdapter adapter;
    OnFriendClickListener listener;

    protected BroadcastReceiver receiver;

    @Override
    public void onResume() {
        super.onResume();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getBundleExtra("information");
                String eventType = intent.getStringExtra("type");

                if ("requested".equalsIgnoreCase(eventType)){
                    onFriendshipRequested(bundle);
                } else if ("accepted".equalsIgnoreCase(eventType)) {
                    onFriendshipUpdated(bundle);
                } else {
                    invalidateAdapterData();
                }

                abortBroadcast();
            }
        };

        registerUpdateReceivers();
    }

    protected abstract void invalidateAdapterData();

    private void registerUpdateReceivers() {
        IntentFilter intentFilter = new IntentFilter("CONTACTS_UPDATE");
        intentFilter.setPriority(500);
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterUpdateReceivers();
    }

    private void unregisterUpdateReceivers() {
        getActivity().unregisterReceiver(receiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generic_recycler_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeAdapter();
    }

    protected void setOnFriendClickListener(OnFriendClickListener listener) {
        this.listener = listener;
    }

    private void initializeAdapter() {
        RecyclerView.LayoutManager linearLayout = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayout);
        adapter = new FriendAdapter(new ArrayList<Friend>());
        if (listener != null) {
            adapter.setOnClickListener(listener);
        }
        recyclerView.setAdapter(adapter);
    }

    protected void onAddedFriends(List<Friend> friends) {
        adapter.clear();
        adapter.addAll(friends);
        adapter.notifyDataSetChanged();
    }

    protected void clearAdapter() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    protected abstract void onFriendshipRequested(Bundle data);

    protected abstract void onFriendshipUpdated(Bundle data);
}
