package cat.xlagunas.conference.data

import android.app.Application
import cat.xlagunas.conference.domain.model.ProxyVideoSink
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoTrack
import javax.inject.Inject

class MediaDataSourceImp @Inject constructor(
    private val application: Application,
    private val peerConnectionFactory: PeerConnectionFactory,
    private val eglContext: EglBase.Context
) {

    val proxyLocalVideoSink = ProxyVideoSink()
    val remoteLocalVideoSink = ProxyVideoSink()

    fun getCameraEnumerator(): CameraEnumerator {
        return if (Camera2Enumerator.isSupported(application)) {
            Camera2Enumerator(application)
        } else {
            Camera1Enumerator(false)
        }
    }

    fun createLocalVideoCapturer(cameraEnumerator: CameraEnumerator): VideoCapturer? {
        if (cameraEnumerator.deviceNames.size == 1) {
            return cameraEnumerator.createCapturer(cameraEnumerator.deviceNames[0], null)
        }
        cameraEnumerator.deviceNames.forEach {
            if (cameraEnumerator.isFrontFacing(it)) {
                return cameraEnumerator.createCapturer(it, null)
            }
        }
        return null
    }

    fun createVideoTrack(capturer: VideoCapturer): VideoTrack {
        val surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglContext)
        val videoSource = peerConnectionFactory.createVideoSource(capturer.isScreencast)
        peerConnectionFactory.createLocalMediaStream("VIDEO")
        capturer.initialize(surfaceTextureHelper, application, videoSource.capturerObserver)
        // TODO Make this dynamic
        capturer.startCapture(1280, 720, 30)

        val localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource)
        localVideoTrack.setEnabled(true)
        localVideoTrack.addSink(proxyLocalVideoSink)
        return localVideoTrack
    }

    companion object {
        const val VIDEO_TRACK_ID: String = "VIDEO_TRACK_ID"
    }
}