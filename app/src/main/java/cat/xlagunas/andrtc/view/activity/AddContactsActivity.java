package cat.xlagunas.andrtc.view.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;

import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.Renderer;
import com.pedrogomez.renderers.RendererBuilder;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.presenter.AddContactsPresenter;
import cat.xlagunas.andrtc.view.SearchListView;
import cat.xlagunas.andrtc.view.model.CurrentFriend;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 14/03/16.
 */
public class AddContactsActivity extends BaseActivity implements SearchListView {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    RVRendererAdapter<Friend> adapter;

    @Inject
    AddContactsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeInjector();
        initializeAdapter();
        presenter.setView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_contact_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_search);
        menuItem.expandActionView();

        final SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    doSearch(newText);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if(TextUtils.isEmpty(searchView.getQuery())){
                    onBackPressed();
                } else {
                    searchView.setQuery("", true);
                }
                return false;
            }
        });

        return true;
    }

    private void initializeInjector() {
        CustomApplication.getApp(this).getUserComponent().inject(this);
    }

    private void initializeAdapter(){
        Renderer<Friend> currentFriendRenderer = new CurrentFriend();
        RendererBuilder<Friend> builder = new RendererBuilder<Friend>(currentFriendRenderer);
        RecyclerView.LayoutManager linearLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);
        adapter = new RVRendererAdapter<>(builder, new ListAdapteeCollection<Friend>());
        recyclerView.setAdapter(adapter);
    }

    private void onAddedFriend(Friend friend){
        adapter.add(friend);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void doSearch(String filter) {
        presenter.search(filter);
    }

    @Override
    public void showEmpty() {
        recyclerView.setVisibility(View.GONE);
        //TODO if there's some view to show when nothing is presented, show it here
    }

    @Override
    public void showList() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void addFriendToList(Friend friend) {
        onAddedFriend(friend);
    }

    @Override
    public void clearAdapter() {
        adapter.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_search:
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
