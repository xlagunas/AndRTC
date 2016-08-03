package cat.xlagunas.andrtc.view.activity;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;

/**
 * Created by xlagunas on 3/8/16.
 */
public class ConferenceActivity extends BaseActivity {
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";

    private static final String TAG = ConferenceActivity.class.getSimpleName();
    @Bind(R.id.local_video_view)
    SurfaceViewRenderer localRenderer;
    CameraVideoCapturer videoCapturer;

//    @Inject
//    ConferencePresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        CustomApplication.getApp(this).getUserComponent().inject(this);
        setContentView(R.layout.activity_conference);

        EglBase eglBase = EglBase.create();

        ButterKnife.bind(this);
        localRenderer.init(eglBase.getEglBaseContext(), null);
        Executor executor = Executors.newSingleThreadScheduledExecutor();

//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
                createCapturer(new Camera2Enumerator(ConferenceActivity.this));
                createVideoTrack();
//                Looper.loop();
                localRenderer.requestLayout();
//            }
//        };
//
//       executor.execute(r);



    }

    private VideoTrack createVideoTrack() {

        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        PeerConnectionFactory factory = new PeerConnectionFactory(null);
        factory.createLocalMediaStream("ARDAMS");

        MediaConstraints videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_WIDTH_CONSTRAINT, Integer.toString(1920)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_WIDTH_CONSTRAINT, Integer.toString(1920)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(720)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(720)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_FPS_CONSTRAINT, Integer.toString(30)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_FPS_CONSTRAINT, Integer.toString(30)));
        VideoSource videoSource = factory.createVideoSource(videoCapturer, videoConstraints);

        VideoTrack localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        localVideoTrack.setEnabled(true);
        localVideoTrack.addRenderer(new VideoRenderer(localRenderer));
        MediaStream stream = factory.createLocalMediaStream("ARDAMS");
        stream.addTrack(localVideoTrack);

        return localVideoTrack;
    }

    private void createCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return;
                }
            }
        }
    }
}
