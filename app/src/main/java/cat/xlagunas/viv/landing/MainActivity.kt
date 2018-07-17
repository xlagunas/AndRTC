package cat.xlagunas.viv.landing

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.viv.R
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mainViewModel: MainViewModel

    @BindView(R.id.bottom_bar)
    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)

        NavigationUI.setupWithNavController(bottomNavigationView, getNavController())
    }

    override fun onSupportNavigateUp() = getNavController().navigateUp()

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    private fun getNavController(): NavController {
        return findNavController(R.id.my_nav_host_fragment)
    }
}