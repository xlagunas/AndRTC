package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.ShowContactsPresenter;
import cat.xlagunas.andrtc.view.ListDataView;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public class CurrentContactFragment extends BaseContactFragment implements ListDataView {

    @Inject
    ShowContactsPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(UserComponent.class).inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.init();
    }

    @Override
    protected void invalidateAdapterData() {
        presenter.updateContacts();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
    }

    @Override
    protected void onFriendshipRequested(Bundle data) {
        Snackbar.make(getView(), getString(R.string.info_contact_requested, data.getString("username")), Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onFriendshipUpdated(Bundle data) {
        Snackbar.make(getView(), getString(R.string.info_contact_accepted, data.getString("username")), Snackbar.LENGTH_LONG).show();
        invalidateAdapterData();
    }

    @Override
    public void showProgress() {
        //TODO //EMPTY
    }

    @Override
    public void addFriends(List<Friend> friends) {
        super.onAddedFriends(friends);
//        adapter.setOnClickListener(new OnFriendClickListener() {
//            @Override
//            public void onItemClicked(int position, Friend item) {
//                startActivity(CallRequestActivity.makeCallerIntent(getContext(), item.getId()));
//            }
//
//            @Override
//            public void onFriendAccepted(Friend friend) {
//
//            }
//
//            @Override
//            public void onFriendRequested(Friend friend) {
//
//            }
//
//            @Override
//            public void onFriendRejected(Friend friend) {
//
//            }
//        });
//
    }

    @Override
    public void showList() {
        recyclerView.setVisibility(View.VISIBLE);
    }
}
