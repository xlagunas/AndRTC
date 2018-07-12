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
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.Injectable
import cat.xlagunas.viv.push.PushTokenViewModel
import javax.inject.Inject

class ContactFragment : Fragment(), Injectable {

    @BindView(R.id.search)
    lateinit var searchView: SearchView

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    private lateinit var pushTokenViewModel: PushTokenViewModel
    private val contactAdapter = ContactAdapter()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var contactViewModel: ContactViewModel


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pushTokenViewModel = ViewModelProviders.of(this, viewModelFactory).get(PushTokenViewModel::class.java)
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
        if (!pushTokenViewModel.isPushTokenRegistered()) {
            pushTokenViewModel.registerToken()
        }
    }

    private fun renderFriends(items: List<Friend>?) {
        contactAdapter.updateAdapter(items!!)
    }
}
