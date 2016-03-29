package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

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
        presenter.init();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
    }

    @Override
    public void showProgress() {
        //TODO //EMPTY
    }

    @Override
    public void addFriend(Friend friend) {
        super.onAddedFriend(friend);
    }

    @Override
    public void showList() {
        recyclerView.setVisibility(View.VISIBLE);
    }
}