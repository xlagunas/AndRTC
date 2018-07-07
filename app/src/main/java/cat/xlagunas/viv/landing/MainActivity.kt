package cat.xlagunas.viv.landing

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController
import butterknife.ButterKnife
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mainViewModel = ViewModelProviders.of(this, (application as VivApplication).viewModelFactory).get(MainViewModel::class.java)
        mainViewModel.isUserLoggedIn.observe(this, Observer { this.sendToLogin() })
    }

    private fun sendToLogin() {
        findNavController(R.id.my_nav_host_fragment).navigate(R.id.action_login)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.my_nav_host_fragment).navigateUp()


}