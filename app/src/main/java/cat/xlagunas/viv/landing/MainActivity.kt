package cat.xlagunas.viv.landing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.core.data.di.viewModelProviderFactory
import cat.xlagunas.viv.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    @BindView(R.id.bottom_bar)
    lateinit var bottomNavigationView: com.google.android.material.bottomnavigation.BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)

        mainViewModel = ViewModelProviders.of(this, viewModelProviderFactory()).get(MainViewModel::class.java)

        NavigationUI.setupWithNavController(bottomNavigationView, getNavController())
    }

    override fun onSupportNavigateUp() = getNavController().navigateUp()

    private fun getNavController(): NavController {
        return findNavController(R.id.my_nav_host_fragment)
    }
}