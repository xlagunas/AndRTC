package cat.xlagunas.conference.ui

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cat.xlagunas.conference.R
import cat.xlagunas.conference.di.ConferenceComponent
import cat.xlagunas.conference.di.DaggerConferenceComponent
import com.google.android.material.snackbar.Snackbar
import com.tbruyelle.rxpermissions2.RxPermissions
import dagger.VivApplication
import org.webrtc.MediaConstraints
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import timber.log.Timber
import javax.inject.Inject

class ConferenceActivity : AppCompatActivity() {

    private lateinit var component: ConferenceComponent
    private lateinit var localSurfaceViewRenderer: SurfaceViewRenderer
    private lateinit var remoteSurfaceViewRenderer: SurfaceViewRenderer

    @Inject
    internal lateinit var viewModelFactory: ConferenceViewModelFactory
    lateinit var conference: ConferenceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        parseRoomFromUriOrFinish(intent.data, this::injectComponent)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference)

        conference =
            ViewModelProviders.of(this, viewModelFactory).get(ConferenceViewModel::class.java)
        MediaSelectorFragment().show(supportFragmentManager, "MediaSelector")

        conference.requestedMedia.observe(this, Observer { mediaConstraints ->
            initVideoRenderers()
            checkPermissions(mediaConstraints)

            conference.conferenceAttendees.observe(this, Observer(onConferencee()))
            conference.localStream.observe(
                this,
                Observer { it.setTarget(localSurfaceViewRenderer) })
            conference.remoteStream.observe(
                this,
                Observer { it.setTarget(remoteSurfaceViewRenderer) })
        })
    }

    private fun injectComponent(roomId: String) {
        component = DaggerConferenceComponent.builder()
            .parent(VivApplication.appComponent(this@ConferenceActivity))
            .roomId(roomId)
            .activity(this@ConferenceActivity)
            .build().apply {
                inject(this@ConferenceActivity)
            }
    }

    private fun initVideoRenderers() {
        localSurfaceViewRenderer =
            findViewById<SurfaceViewRenderer>(R.id.local_renderer).apply {
                init(conference.getEGLContext(), null)
                setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
                setZOrderMediaOverlay(true)
                setEnableHardwareScaler(true /* enabled */)
            }

        remoteSurfaceViewRenderer =
            findViewById<SurfaceViewRenderer>(R.id.remote_renderer).apply {
                init(conference.getEGLContext(), null)
                this.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
                setEnableHardwareScaler(true /* enabled */)
            }
    }

    private fun parseRoomFromUriOrFinish(data: Uri?, block: (String) -> Unit) {
        val roomId = data?.getQueryParameter("roomId")

        if (roomId != null) {
            block(roomId)
        } else {
            Timber.w("Room can't be parsed from uri, closing activity")
            finish()
        }
    }

    private fun onConferencee(): (Int) -> Unit {
        return {
            Snackbar.make(
                findViewById<FrameLayout>(android.R.id.content),
                "Joined room with other $it attendants",
                Snackbar.LENGTH_INDEFINITE
            ).show()
        }
    }

    @SuppressLint("CheckResult")
    private fun checkPermissions(mediaConstraints: MediaConstraints) {
        val rxPermissions = RxPermissions(this)
        rxPermissions.requestEachCombined(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
            .subscribe { permission ->
                if (permission.granted) {
                    conference.onStart(mediaConstraints)
                } else if (permission.shouldShowRequestPermissionRationale) {
                    //TODO At least one denied permission without ask never again
                } else {
                    // TODO At least one denied permission with ask never again Need to go to the settings
                }
            }
    }
}
