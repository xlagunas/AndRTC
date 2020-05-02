package cat.xlagunas.viv.home

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProviders
import cat.xlagunas.contact.ui.ContactFragment
import cat.xlagunas.core.common.viewModelProviderFactory
import cat.xlagunas.user.login.LoginFragment
import cat.xlagunas.user.profile.ProfileFragment
import cat.xlagunas.viv.R
import cat.xlagunas.viv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportFragmentManager.registerFragmentLifecycleCallbacks(callbacks, false)

        if (supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) == null) {
            supportFragmentManager.commit {
                add<ContactFragment>(R.id.my_nav_host_fragment, ContactFragment::class.simpleName)
            }
        }

        mainViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory()).get(MainViewModel::class.java)

        binding.content.bottomBar.setOnNavigationItemSelectedListener {
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
                binding.content.bottomBar.selectedItemId = nextItem
            }
        }
    }

    private fun ifNotSelected(@LayoutRes menuId: Int, action: (Int) -> Unit) {
        if (binding.content.bottomBar.selectedItemId != menuId) {
            action.invoke(menuId)
        }
    }
}
