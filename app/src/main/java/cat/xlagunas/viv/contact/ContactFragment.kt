package cat.xlagunas.viv.contact

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.domain.commons.Friend
import cat.xlagunas.viv.ContactViewModel
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import cat.xlagunas.viv.contact.search.SearchViewModel
import cat.xlagunas.viv.login.LoginFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class ContactFragment : Fragment() {

    @BindView(R.id.search)
    lateinit var searchView: SearchView

    private lateinit var contactViewModel: ContactViewModel
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactViewModel = ViewModelProviders.of(this, (activity!!.application as VivApplication).viewModelFactory).get(ContactViewModel::class.java)
        searchViewModel = ViewModelProviders.of(this, (activity!!.application as VivApplication).viewModelFactory).get(SearchViewModel::class.java)
        contactViewModel.displayState.observe(this, Observer<DisplayState> {
            when (it) {
                is NotRegistered -> findNavController().navigate(R.id.action_login)
                is Display -> setUpActivity(it)
                is Error -> Snackbar.make(activity!!.findViewById(android.R.id.content), it.message, Snackbar.LENGTH_LONG).show()
                is Loading -> {
                }
            }
        })
        contactViewModel.getUserInfo()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)

    }

    private fun setUpActivity(it: Display) {
        ButterKnife.bind(this, view!!)
        activity!!.toolbar.title = String.format("Welcome %s", it.user.firstName)

        checkPermission()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.isNotEmpty().let {
                    searchViewModel.findContact(query!!).observe(this@ContactFragment, Observer { Timber.d("Search returned ${it?.size} elements") })
                    return true
                }
            }

            override fun onQueryTextChange(newText: String?) = false
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LoginFragment.LOGIN_RESULT -> if (resultCode == Activity.RESULT_OK) {
                contactViewModel.getUserInfo()
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), 1000)
        } else {
            contactViewModel.phoneContactDataSource()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1000 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    contactViewModel.phoneContactDataSource()
                } else {
                    Timber.w("Error we don't have permission to show contacts")
                }
                return
            }
        }
    }
}