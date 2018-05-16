package cat.xlagunas.viv.contact

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.viv.ContactViewModel
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import cat.xlagunas.viv.login.LoginActivity
import timber.log.Timber


class ContactActivity : AppCompatActivity() {

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    private lateinit var contactViewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactViewModel = ViewModelProviders.of(this, (application as VivApplication).viewModelFactory).get(ContactViewModel::class.java)
        contactViewModel.displayState.observe(this, Observer<DisplayState> {
            when (it) {
                is NotRegistered -> startActivityForResult(Intent(this, LoginActivity::class.java), LoginActivity.LOGIN_RESULT)
                is Display -> setUpActivity(it)
                is Error -> Snackbar.make(findViewById(android.R.id.content), it.message, Snackbar.LENGTH_LONG).show()
                is Loading -> {
                }
            }
        })
        contactViewModel.getUserInfo()

    }

    private fun setUpActivity(it: Display) {
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_contact)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        toolbar.title = String.format("Welcome %s", it.user.firstName)
        checkPermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LoginActivity.LOGIN_RESULT -> if (resultCode == Activity.RESULT_OK) {
                contactViewModel.getUserInfo()
            }
        }
    }

    private fun checkPermission(){
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        1000)
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
