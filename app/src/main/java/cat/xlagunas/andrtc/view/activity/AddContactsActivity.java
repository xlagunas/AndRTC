package cat.xlagunas.andrtc.view.activity;
;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;

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
    @Bind(R.id.search_view)
    SearchView searchView;

    RVRendererAdapter<Friend> adapter;

    @Inject
    AddContactsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initializeInjector();
        initializeAdapter();
        presenter.setView(this);
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
    protected void onResume() {
        super.onResume();
        presenter.search("lagunas");
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
}
