package cat.xlagunas.viv.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.core.di.Injectable
import cat.xlagunas.core.di.VivApplication
import cat.xlagunas.core.domain.entity.Friend
import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.domain.call.Call
import cat.xlagunas.viv.R
import cat.xlagunas.viv.push.PushTokenPresenter
import dagger.DaggerMonolythComponent
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class ContactFragment : Fragment(), Injectable, ContactListener {

    @BindView(R.id.search)
    lateinit var searchView: SearchView

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var pushTokenPresenter: PushTokenPresenter

    private lateinit var contactViewModel: ContactViewModel

    private val contactAdapter by lazy { ContactAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
    }

    protected fun inject() {
        DaggerMonolythComponent.builder()
            .withParentComponent(VivApplication.appComponent(context!!)).build()
            .inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        contactViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ContactViewModel::class.java)
        contactViewModel.contacts.observe(this, Observer(this::renderFriends))
        registerPushToken()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        contactViewModel.observeCall(listOf(friend)).observe(this, Observer(this::handleCall))
    }

    fun handleCall(call: Call) {
        Timber.d("Call Successfully created with id: ${call.id}")
        startActivity(ContactUtils.generateCallIntent(call.id, context!!))
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                query.isNotEmpty().let {
                    contactViewModel.findContact(query)
                        .observe(
                            this@ContactFragment,
                            Observer(this@ContactFragment::renderFriends)
                        )
                    return true
                }
            }

            override fun onQueryTextChange(newText: String) = false
        })
    }

    private fun setUpRecyclerView() {
        recyclerView.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                activity,
                RecyclerView.VERTICAL,
                false
            )
            adapter = contactAdapter
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    this@ContactFragment.activity,
                    LinearLayout.VERTICAL
                )
            )
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
