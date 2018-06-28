package cat.xlagunas.viv.contact

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
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
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.ContactViewModel
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import cat.xlagunas.viv.push.PushTokenViewModel
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class ContactFragment : Fragment() {

    @BindView(R.id.search)
    lateinit var searchView: SearchView

    @BindView(R.id.recycler_view)
    lateinit var recyclerView: RecyclerView

    private lateinit var contactViewModel: ContactViewModel
    private lateinit var pushTokenViewModel: PushTokenViewModel
    private val contactAdapter = ContactAdapter()

    private lateinit var friendshipViewModel: FriendshipViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactViewModel = ViewModelProviders.of(this, (activity!!.application as VivApplication).viewModelFactory).get(ContactViewModel::class.java)
        pushTokenViewModel = ViewModelProviders.of(this, (activity!!.application as VivApplication).viewModelFactory).get(PushTokenViewModel::class.java)
        friendshipViewModel = ViewModelProviders.of(this, (activity!!.application as VivApplication).viewModelFactory).get(FriendshipViewModel::class.java)
        contactViewModel.getUserInfo.observe(this, handleDisplayState())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    private fun handleDisplayState(): Observer<DisplayState> {
        return Observer {
            when (it) {
                is NotRegistered -> findNavController().navigate(R.id.action_login)
                is Display -> setUpActivity(it)
                is Error -> Snackbar.make(activity!!.findViewById(android.R.id.content), it.message, Snackbar.LENGTH_LONG).show()
                is Loading -> Timber.d("ContactFragment on Loading state")
            }
        }
    }

    private fun setUpActivity(display: Display) {
        ButterKnife.bind(this, view!!)

        activity!!.toolbar.title = String.format("Welcome %s", display.user.firstName)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
            adapter = contactAdapter
            addItemDecoration(DividerItemDecoration(this@ContactFragment.activity!!, LinearLayout.VERTICAL))
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.isNotEmpty().let {
                    friendshipViewModel.findContact(query!!)
                            .observe(this@ContactFragment, Observer(this@ContactFragment::renderFriends))
                    return true
                }
            }

            override fun onQueryTextChange(newText: String?) = false
        })

        friendshipViewModel.getContacts.observe(this, Observer(this@ContactFragment::renderFriends))

        registerPushToken()
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
