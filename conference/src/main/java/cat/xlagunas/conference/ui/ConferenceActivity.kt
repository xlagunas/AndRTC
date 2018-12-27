package cat.xlagunas.conference.ui

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cat.xlagunas.conference.R
import cat.xlagunas.conference.data.utils.UUIDUserSession
import cat.xlagunas.conference.di.ConferenceComponent
import cat.xlagunas.conference.di.DaggerConferenceComponent
import cat.xlagunas.conference.domain.model.Conferencee
import cat.xlagunas.core.di.VivApplication
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class ConferenceActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: ConferenceViewModelFactory

    lateinit var conference: ConferenceViewModel

    lateinit var component: ConferenceComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        component = DaggerConferenceComponent.builder()
            .parent(VivApplication.appComponent(this))
            .roomId("christmasRoom")
            .userSession(UUIDUserSession())
            .activity(this)
            .build().apply {
                inject(this@ConferenceActivity)
            }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference)
        conference = ViewModelProviders.of(this, viewModelFactory).get(ConferenceViewModel::class.java)
            .apply { onStart() }
        conference.conferenceAttendees.observe(this, Observer(onConferencee()))
    }

    private fun onConferencee(): (List<Conferencee>) -> Unit =
        {
            Snackbar.make(
                findViewById<FrameLayout>(android.R.id.content),
                "Joined room with other ${it.size} attendants",
                Snackbar.LENGTH_INDEFINITE
            ).show()
        }

/*
    override fun onSupportNavigateUp() = getNavController().navigateUp()

    private fun getNavController(): NavController {
        return findNavController(cat.xlagunas.viv.R.id.my_nav_host_fragment)
    }*/
}
