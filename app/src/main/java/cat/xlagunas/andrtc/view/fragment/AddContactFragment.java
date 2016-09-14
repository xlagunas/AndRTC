package cat.xlagunas.andrtc.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.AddContactsPresenter;
import cat.xlagunas.andrtc.view.SearchListView;
import cat.xlagunas.andrtc.view.util.NOOPFriendClickListener;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 19/03/16.
 */
public class AddContactFragment extends BaseContactFragment implements SearchListView {

    private static final String TAG = AddContactFragment.class.getSimpleName();

    private SearchView searchView;

    @Inject
    AddContactsPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getComponent(UserComponent.class).inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        setOnFriendClickListener(new NOOPFriendClickListener() {

            @Override
            public void onFriendRequested(Friend friend) {
                presenter.requestFriendship(friend);
            }

            @Override
            public void onFriendAccepted(Friend friend) {
                presenter.acceptFriendship(friend);
            }

            @Override
            public void onFriendRejected(Friend friend) {
                presenter.rejectFriendship(friend);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.resume();
    }

    @Override
    protected void invalidateAdapterData() {
        presenter.updateContacts();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_contact_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.expandActionView();

        searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(onSearchListener);

        MenuItemCompat.setOnActionExpandListener(menuItem, onExpandActionListener);
    }

    @Override
    public void doSearch(String filter) {
        presenter.search(filter);
    }

    @Override
    public void showEmpty() {
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showList() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void addFriends(List<Friend> friends) {
        onAddedFriends(friends);
    }

    @Override
    public void clearAdapter() {
        super.clearAdapter();
    }

    @Override
    protected void onFriendshipRequested(Bundle bundle) {
        Snackbar.make(getView(),
                getString(R.string.info_contact_requested,
                bundle.getString("username")), Snackbar.LENGTH_SHORT)
                .show();
        invalidateAdapterData();
    }

    @Override
    protected void onFriendshipUpdated(Bundle bundle) {
        Snackbar.make(getView(),
                getString(R.string.info_contact_accepted, bundle.getString("username")),
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showConfirmationError(Throwable e) {
        Log.e(TAG, "Error adding new friendship", e);
    }

    @Override
    public void showConfirmation() {
        Toast.makeText(getActivity(), "New friendship requested", Toast.LENGTH_LONG).show();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void notifyContactUpdate(Friend friend, String message) {
        presenter.updateContacts();
    }

    @Override
    public void notifiyUpdateError(Friend friend, Throwable e) {
        Log.e(TAG, "Error updating notification error: " + e);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
            case R.id.menu_search:
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private SearchView.OnQueryTextListener onSearchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            doSearch(newText);
            return true;
        }

    };

    private MenuItemCompat.OnActionExpandListener onExpandActionListener = new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return true;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            if (TextUtils.isEmpty(searchView.getQuery())) {
                getActivity().onBackPressed();
            } else {
                searchView.setQuery("", true);
            }
            return false;
        }
    };


}
