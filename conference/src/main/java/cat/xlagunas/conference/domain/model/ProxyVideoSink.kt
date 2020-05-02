package cat.xlagunas.conference.domain.model

import org.webrtc.VideoFrame
import org.webrtc.VideoSink
import timber.log.Timber

class ProxyVideoSink : VideoSink {
    private var target: VideoSink? = null

    @Synchronized
    override fun onFrame(frame: VideoFrame) {
        if (target == null) {
            Timber.d("Dropping frame in proxy because target is null.")
            return
        }

        target?.onFrame(frame)
    }

    @Synchronized
    fun setTarget(target: VideoSink?) {
        this.target = target
    }
}
