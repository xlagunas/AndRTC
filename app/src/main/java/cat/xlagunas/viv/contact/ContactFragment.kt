package cat.xlagunas.viv.contact

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
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
import androidx.navigation.fragment.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.Injectable
import cat.xlagunas.viv.push.PushTokenPresenter
import javax.inject.Inject

@OpenForTesting
class ContactFragment : Fragment(), Injectable, ContactListener {

    @BindView(R.id.search)
    lateinit var searchView: SearchView

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var pushTokenPresenter: PushTokenPresenter

    private lateinit var contactViewModel: ContactViewModel

    private val contactAdapter by lazy { ContactAdapter(this) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        contactViewModel = ViewModelProviders.of(this, viewModelFactory).get(ContactViewModel::class.java)
        contactViewModel.contacts.observe(this, Observer(this::renderFriends))
        registerPushToken()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        setUpRecyclerView()
        setupSearchView()
    }

    override fun onContactRequested(friend: Friend) {
        contactViewModel.addContact(friend)
    }

    override fun onContactAccepted(friend: Friend) {
        contactViewModel.acceptContactRequest(friend)
    }

    override fun onContactRejected(friend: Friend) {
        contactViewModel.rejectContactRequest(friend)
    }

    override fun onContactCalled(friend: Friend) {
        contactViewModel.callFriend(friend)
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                query.isNotEmpty().let {
                    contactViewModel.findContact(query)
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
        if (!pushTokenPresenter.isPushTokenRegistered()) {
            pushTokenPresenter.registerToken()
        }
    }

    private fun renderFriends(items: List<Friend>?) {
        contactAdapter.updateAdapter(items!!)
    }

    fun navController() = findNavController()
}
