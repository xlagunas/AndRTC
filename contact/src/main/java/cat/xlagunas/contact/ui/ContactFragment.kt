package cat.xlagunas.contact.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.contact.R
import cat.xlagunas.contact.R2
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.ContactUtils
import cat.xlagunas.core.data.di.viewModelProviderFactory
import cat.xlagunas.core.di.Injectable
import cat.xlagunas.core.domain.entity.Call
import cat.xlagunas.core.domain.entity.Friend
import timber.log.Timber

@OpenForTesting
class ContactFragment : Fragment(), Injectable, ContactListener {

    @BindView(R2.id.search)
    lateinit var searchView: SearchView

    @BindView(R2.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    private lateinit var contactViewModel: ContactViewModel

    private val contactAdapter by lazy { ContactAdapter(this) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        contactViewModel = ViewModelProviders.of(this, viewModelProviderFactory())
            .get(ContactViewModel::class.java)
        contactViewModel.contacts.observe(viewLifecycleOwner, Observer(this::renderFriends))
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
        requireActivity().startActivity(ContactUtils.generateRoomIntent(call))
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

    private fun renderFriends(items: List<Friend>?) {
        contactAdapter.updateAdapter(items!!)
    }

    fun navController() = findNavController()
}
