package cat.xlagunas.contact.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import cat.xlagunas.contact.databinding.FragmentContactBinding
import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.viewModel
import timber.log.Timber

@OpenForTesting
class ContactFragment : Fragment(), ContactListener {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    internal lateinit var contactViewModel: ContactViewModel
    private val contactAdapter by lazy { ContactAdapter(this) }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        contactViewModel = viewModel(ContactViewModel::class.java)
        contactViewModel.contacts.observe(viewLifecycleOwner, Observer(this::renderFriends))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        contactViewModel.createCall(listOf(friend))
    }

    private fun setupSearchView() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                query.isNotEmpty().let {
                    contactViewModel.findContact(query)
                        .observe(
                            this@ContactFragment,
                            Observer { renderFriends(it) }
                        )
                    return true
                }
            }

            override fun onQueryTextChange(newText: String) = false
        })
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
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

    private fun renderFriends(items: Result<List<Friend>>) {
        items.fold(contactAdapter::updateAdapter, Timber::e)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
