package cat.xlagunas.conference.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import cat.xlagunas.viv.conference.R
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class ConferenceActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference)
    }

    override fun onSupportNavigateUp() = getNavController().navigateUp()

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    private fun getNavController(): NavController {
        return findNavController(cat.xlagunas.viv.R.id.my_nav_host_fragment)
    }
}
