package cat.xlagunas.viv.contact

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.domain.commons.User
import cat.xlagunas.viv.UserViewModel
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import cat.xlagunas.viv.push.PushTokenViewModel


class ContactFragment : Fragment() {

    @BindView(R.id.search)
    lateinit var searchView: SearchView

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    private lateinit var pushTokenViewModel: PushTokenViewModel
    private val contactAdapter = ContactAdapter()

    private lateinit var friendshipViewModel: FriendshipViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pushTokenViewModel = ViewModelProviders.of(this, (activity!!.application as VivApplication).viewModelFactory).get(PushTokenViewModel::class.java)
        friendshipViewModel = ViewModelProviders.of(this, (activity!!.application as VivApplication).viewModelFactory).get(FriendshipViewModel::class.java)
        userViewModel = ViewModelProviders.of(this, (activity!!.application as VivApplication).viewModelFactory).get(UserViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        setUpRecyclerView()
        setupSearchView()
        registerPushToken()

        Transformations
                .switchMap(userViewModel.getCurrentUser(), this::observeFriends)
                .observe(this, Observer(this::renderFriends))
    }

    private fun observeFriends(u: User): LiveData<List<Friend>> {
        return friendshipViewModel.getContacts
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                query.isNotEmpty().let {
                    friendshipViewModel.findContact(query)
                            .observe(this@ContactFragment, Observer(this@ContactFragment::renderFriends))
                    return true
                }
            }

            override fun onQueryTextChange(newText: String) = false
        })
    }

    private fun setUpRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
            adapter = contactAdapter
            addItemDecoration(DividerItemDecoration(this@ContactFragment.activity, LinearLayout.VERTICAL))
        }
    }

    private fun registerPushToken() {
        if (!pushTokenViewModel.isPushTokenRegistered()) {
            pushTokenViewModel.registerToken()
        }
    }

    private fun renderFriends(items: List<Friend>?) {
        contactAdapter.updateAdapter(items!!)
    }
}
