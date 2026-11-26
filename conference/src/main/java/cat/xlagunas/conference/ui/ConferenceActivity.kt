package cat.xlagunas.conference.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.conference.R
import cat.xlagunas.conference.di.ConferenceComponent
import cat.xlagunas.conference.di.DaggerConferenceComponent
import cat.xlagunas.viv.dagger.VivApplication
import com.google.android.material.snackbar.Snackbar
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

    private var pendingConstraints: MediaConstraints? = null
    private var requestedPermissions: Array<String> = emptyArray()
    private var permissionsRequestInFlight = false
    private var permissionsSnackbar: Snackbar? = null
    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), ::onPermissionsResult)
    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // Back from Settings: request again. Granted permissions return synchronously and start the call;
            // otherwise the denied Snackbar comes back.
            if (pendingConstraints != null && requestedPermissions.isNotEmpty()) {
                launchPermissionsRequest()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        parseRoomFromUriOrFinish(intent.data, this::injectComponent)
        super.onCreate(savedInstanceState)
        permissionsRequestInFlight =
            savedInstanceState?.getBoolean(KEY_PERMISSIONS_REQUEST_IN_FLIGHT) ?: false
        setContentView(R.layout.activity_conference)

        conference =
            ViewModelProvider(this, viewModelFactory).get(ConferenceViewModel::class.java)
        MediaSelectorFragment().show(supportFragmentManager, "MediaSelector")

        conference.requestedMedia.observe(
            this,
            Observer { mediaConstraints ->
                initVideoRenderers()
                checkPermissions(mediaConstraints)

                conference.conferenceAttendees.observe(this, Observer(onConferencee()))
                conference.localStream.observe(
                    this,
                    Observer { it.setTarget(localSurfaceViewRenderer) },
                )
                conference.remoteStream.observe(
                    this,
                    Observer { it.setTarget(remoteSurfaceViewRenderer) },
                )
            },
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_PERMISSIONS_REQUEST_IN_FLIGHT, permissionsRequestInFlight)
    }

    private fun injectComponent(roomId: String) {
        component =
            DaggerConferenceComponent.builder()
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
                setEnableHardwareScaler(true)
            }

        remoteSurfaceViewRenderer =
            findViewById<SurfaceViewRenderer>(R.id.remote_renderer).apply {
                init(conference.getEGLContext(), null)
                this.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
                setEnableHardwareScaler(true)
            }
    }

    private fun parseRoomFromUriOrFinish(
        data: Uri?,
        block: (String) -> Unit,
    ) {
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
                Snackbar.LENGTH_INDEFINITE,
            ).show()
        }
    }

    private fun checkPermissions(mediaConstraints: MediaConstraints) {
        pendingConstraints = mediaConstraints
        requestedPermissions = requiredPermissions(mediaConstraints)

        if (requestedPermissions.isEmpty()) {
            conference.onStart(mediaConstraints)
        } else if (!permissionsRequestInFlight) {
            launchPermissionsRequest()
        }
    }

    private fun launchPermissionsRequest() {
        permissionsRequestInFlight = true
        permissionsLauncher.launch(requestedPermissions)
    }

    private fun onPermissionsResult(result: Map<String, Boolean>) {
        permissionsRequestInFlight = false
        val outcome =
            permissionsOutcome(
                requested = requestedPermissions,
                result = result,
                // Null when the result arrives before requestedMedia re-emits after a recreation;
                // that re-emission requests again.
                hasPendingRequest = pendingConstraints != null,
                shouldShowRationale = ::shouldShowRequestPermissionRationale,
            )
        if (outcome != PermissionsOutcome.Start && outcome != PermissionsOutcome.Ignore) {
            Timber.w("Conference permissions not granted (%s): %s", outcome, result)
        }

        when (outcome) {
            PermissionsOutcome.Start -> {
                permissionsSnackbar?.dismiss()
                pendingConstraints?.let(conference::onStart)
            }
            // A real result that follows an empty (cancelled) one replaces this Snackbar.
            PermissionsOutcome.ShowRetry ->
                showPermissionsSnackbar(
                    R.string.conference_permissions_rationale,
                    R.string.conference_permissions_retry,
                ) { launchPermissionsRequest() }
            PermissionsOutcome.ShowSettings ->
                showPermissionsSnackbar(
                    R.string.conference_permissions_denied_permanently,
                    R.string.conference_permissions_settings,
                ) { openAppSettings() }
            PermissionsOutcome.Ignore -> Unit
        }
    }

    private fun showPermissionsSnackbar(
        @StringRes message: Int,
        @StringRes action: Int,
        onAction: () -> Unit,
    ) {
        permissionsSnackbar?.dismiss()
        permissionsSnackbar =
            Snackbar.make(
                findViewById<FrameLayout>(android.R.id.content),
                message,
                Snackbar.LENGTH_INDEFINITE,
            ).setAction(action) { onAction() }.also { it.show() }
    }

    private fun openAppSettings() {
        settingsLauncher.launch(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null),
            ),
        )
    }

    private companion object {
        const val KEY_PERMISSIONS_REQUEST_IN_FLIGHT = "permissions_request_in_flight"
    }
}
