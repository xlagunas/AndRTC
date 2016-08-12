package cat.xlagunas.andrtc.view;

import org.webrtc.SurfaceViewRenderer;

import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManagerImpl;

/**
 * Created by xlagunas on 12/8/16.
 */
public interface ConferenceDataView extends WebRTCManagerImpl.ConferenceListener {
    void updateLayout();
    void startCameraStream();

    void removeRenderer(SurfaceViewRenderer remoteRenderer);
}
