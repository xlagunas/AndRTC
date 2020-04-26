package cat.xlagunas.viv.landing

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.contact.ui.ContactFragment
import cat.xlagunas.core.data.di.viewModelProviderFactory
import cat.xlagunas.viv.R
import cat.xlagunas.viv.login.LoginFragment
import cat.xlagunas.viv.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    @BindView(R.id.bottom_bar)
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        supportFragmentManager.registerFragmentLifecycleCallbacks(callbacks, false)

        supportFragmentManager.commit {
            add<ContactFragment>(R.id.my_nav_host_fragment, ContactFragment::class.simpleName)
        }
        mainViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory()).get(MainViewModel::class.java)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            ifNotSelected(it.itemId) { itemId ->
                when (itemId) {
                    R.id.action_contact -> mainViewModel.navigateToContacts()
                    R.id.action_profile -> mainViewModel.navigateToProfile()
                }
            }
            true
        }
    }

    private val callbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            val nextItem = when (f) {
                is ContactFragment -> R.id.action_contact
                is LoginFragment, is ProfileFragment -> R.id.action_profile
                else -> -1
            }
            if (nextItem < 0) return
            ifNotSelected(nextItem) {
                bottomNavigationView.selectedItemId = nextItem
            }
        }
    }

    private fun ifNotSelected(@LayoutRes menuId: Int, action: (Int) -> Unit) {
        if (bottomNavigationView.selectedItemId != menuId) {
            action.invoke(menuId)
        }
    }
}