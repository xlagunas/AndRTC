package cat.xlagunas.andrtc.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.Logging;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManager;
import cat.xlagunas.andrtc.data.net.webrtc.WebRTCManagerImpl;
import cat.xlagunas.andrtc.di.modules.ConferenceModule;

/**
 * Created by xlagunas on 3/8/16.
 */
public class ConferenceActivity extends BaseActivity implements WebRTCManagerImpl.ConferenceListener {
    private static final String EXTRA_ROOM_ID = "room_id";

    private static final String TAG = ConferenceActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST_CODE = 200;
    private VideoSource videoSource;

    @Bind(R.id.local_video_view)
    SurfaceViewRenderer localRenderer;
    CameraVideoCapturer videoCapturer;

    @Bind(R.id.remote_vide_view)
    SurfaceViewRenderer remoteRenderer;

    @Inject
    Executor executor;

    @Inject
    WebRTCManager manager;

    private boolean startedLocalStream = false;

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

        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, CAMERA_REQUEST_CODE);
        } else {
            init();
        }

    }

    private void init() {
        manager.setConferenceListener(this);
        manager.init();
        createCapturer(new Camera2Enumerator(ConferenceActivity.this));
        manager.initLocalSource(localRenderer, videoCapturer);
        manager.setRemoteRendererSource(remoteRenderer);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            init();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initLocalVideo();
    }

    private void initLocalVideo() {
        if (videoSource != null && !startedLocalStream){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    videoSource.restart();
                }
            });
            startedLocalStream = true;
        }
    }

    @Override
    protected void onPause() {
        stopLocalVideo();
        super.onPause();

    }

    private void stopLocalVideo() {
        if (videoSource != null && startedLocalStream) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    videoSource.stop();
                }
            });
            startedLocalStream = false;
        }
    }

    @Override
    protected void onDestroy() {
        if (localRenderer != null){
            localRenderer.release();
            localRenderer = null;
        }
        if (videoCapturer != null) {
            videoCapturer.dispose();
            videoCapturer = null;
        }

        manager.stop();
        super.onDestroy();
    }

    private void createCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find back facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
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
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front camera capturer.");
                videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return;
                }
            }
        }
    }

    @Override
    public void onLocalVideoGenerated(VideoSource videoSource) {
        this.videoSource = videoSource;
        initLocalVideo();
    }

}
