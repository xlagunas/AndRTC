package cat.xlagunas.conference.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import cat.xlagunas.viv.conference.R
import javax.inject.Inject

class ConferenceActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference)
    }

    override fun onSupportNavigateUp() = getNavController().navigateUp()

    private fun getNavController(): NavController {
        return findNavController(cat.xlagunas.viv.R.id.my_nav_host_fragment)
    }
}
