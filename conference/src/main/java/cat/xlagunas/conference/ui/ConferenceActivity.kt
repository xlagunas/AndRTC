package cat.xlagunas.conference.ui

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cat.xlagunas.conference.R
import cat.xlagunas.conference.di.ConferenceComponent
import cat.xlagunas.conference.di.DaggerConferenceComponent
import cat.xlagunas.core.di.VivApplication
import com.google.android.material.snackbar.Snackbar
import org.webrtc.RendererCommon
import org.webrtc.SurfaceViewRenderer
import timber.log.Timber
import javax.inject.Inject

class ConferenceActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: ConferenceViewModelFactory

    lateinit var conference: ConferenceViewModel

    lateinit var component: ConferenceComponent

    private lateinit var localSurfaceViewRenderer: SurfaceViewRenderer

    private lateinit var remoteSurfaceViewRenderer: SurfaceViewRenderer

    override fun onCreate(savedInstanceState: Bundle?) {

        parseRoomFromUriOrFinish(intent.data) {
            component = DaggerConferenceComponent.builder()
                    .parent(VivApplication.appComponent(this@ConferenceActivity))
                    .roomId(it)
                    .activity(this@ConferenceActivity)
                    .build().apply {
                        inject(this@ConferenceActivity)
                    }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference)
        conference = ViewModelProviders.of(this, viewModelFactory).get(ConferenceViewModel::class.java)
        // TODO NOT HAPPY WITH HOW THE CONTEXT NEEDS TO BE PASSED
        localSurfaceViewRenderer = findViewById<SurfaceViewRenderer>(R.id.local_renderer).apply {
            init(conference.getEGLContext(), null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setZOrderMediaOverlay(true)
            setEnableHardwareScaler(true /* enabled */)
        }

        remoteSurfaceViewRenderer = findViewById<SurfaceViewRenderer>(R.id.remote_renderer).apply {
            init(conference.getEGLContext(), null)
            this.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            setEnableHardwareScaler(true /* enabled */)
        }
        checkPermissions()
        conference.conferenceAttendees.observe(this, Observer(onConferencee()))
        conference.localStream.observe(this, Observer { it.setTarget(localSurfaceViewRenderer) })
        conference.remoteStream.observe(this, Observer { it.setTarget(remoteSurfaceViewRenderer) })
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

    private fun init() {
        conference.onStart()
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

    private fun checkPermissions() {
        var arePermissionsOk = true
        var permissionsToAsk: MutableList<String>? = null

        for (i in 0 until PERMISSIONS.size) {
            if (ContextCompat.checkSelfPermission(
                            this,
                            PERMISSIONS[i]
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                arePermissionsOk = false
                if (permissionsToAsk == null) {
                    permissionsToAsk = ArrayList()
                }
                permissionsToAsk.add(PERMISSIONS[i])
            }
        }

        if (arePermissionsOk) {
            init()
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToAsk!!.toTypedArray(),
                    CONFERENCE_REQUEST_CODE
            )
        }
    }

    companion object {
        val PERMISSIONS = arrayOf("android.permission.RECORD_AUDIO", "android.permission.CAMERA")
        const val CONFERENCE_REQUEST_CODE = 1010
    }

    // TODO CHECK THIS LOGIC, IS REALLY COMPLEX FOR WHAT IT NEEDS TO DO
    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONFERENCE_REQUEST_CODE) {
            var arePermissionsGranted = true

            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    arePermissionsGranted = false
                }
            }
            if (arePermissionsGranted) {
                init()
            } else {
                Toast.makeText(this, "some permissions are not accepted, can't go on!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
