package cat.xlagunas.andrtc.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManager;
import cat.xlagunas.andrtc.di.modules.ConferenceModule;

/**
 * Created by xlagunas on 3/8/16.
 */
public class ConferenceActivity extends BaseActivity {
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    private static final String EXTRA_ROOM_ID = "room_id";

    private static final String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    private static final String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    private static final String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    private static final String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    private static final String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    private static final String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";

    private static final String TAG = ConferenceActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST_CODE = 200;
    private VideoSource videoSource;

    @Bind(R.id.local_video_view)
    SurfaceViewRenderer localRenderer;
    CameraVideoCapturer videoCapturer;

    @Inject
    Executor executor;
    @Inject
    EglBase eglBase;

    @Inject
    WebRTCManager manager;

    public static Intent startActivity(Context context, String roomId){
        Intent intent = new Intent(context, ConferenceActivity.class);
        intent.putExtra(EXTRA_ROOM_ID, roomId);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference);

        String room = getIntent().getStringExtra(EXTRA_ROOM_ID);

        CustomApplication.getApp(this).getUserComponent().plus(new ConferenceModule(EglBase.create(), room)).inject(this);

        ButterKnife.bind(this);
        manager.init();
        localRenderer.init(eglBase.getEglBaseContext(), null);

        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, CAMERA_REQUEST_CODE);
        } else {
            startCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startCamera();
        }
    }

    private void startCamera() {
        Log.d(TAG, "Starting camera");
        createCapturer(new Camera2Enumerator(ConferenceActivity.this));
        createVideoTrack();
        localRenderer.requestLayout();
    }

    private VideoTrack createVideoTrack() {

        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);
        PeerConnectionFactory factory = new PeerConnectionFactory(new PeerConnectionFactory.Options());
        factory.setVideoHwAccelerationOptions(eglBase.getEglBaseContext(), null);

        MediaConstraints videoConstraints = new MediaConstraints();
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_WIDTH_CONSTRAINT, Integer.toString(1280)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_WIDTH_CONSTRAINT, Integer.toString(1280)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(720)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_HEIGHT_CONSTRAINT, Integer.toString(720)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MIN_VIDEO_FPS_CONSTRAINT, Integer.toString(30)));
        videoConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                MAX_VIDEO_FPS_CONSTRAINT, Integer.toString(30)));
        videoSource = factory.createVideoSource(videoCapturer, videoConstraints);

        VideoTrack localVideoTrack = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        localVideoTrack.setEnabled(true);
        localVideoTrack.addRenderer(new VideoRenderer(localRenderer));

        return localVideoTrack;
    }

    @Override
    protected void onResume() {
        super.onResume();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                videoSource.restart();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                videoSource.stop();
            }
        });
    }

    @Override
    protected void onDestroy() {
        videoSource.dispose();
        super.onDestroy();
    }

    private void createCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find back facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating back facing camera capturer.");
                videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return;
                }
            }
        }

        // Front back camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front camera capturer.");
                videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return;
                }
            }
        }
    }
}
