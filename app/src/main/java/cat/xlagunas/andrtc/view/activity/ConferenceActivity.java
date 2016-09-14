package cat.xlagunas.andrtc.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.modules.ConferenceModule;
import cat.xlagunas.andrtc.presenter.ConferencePresenter;
import cat.xlagunas.andrtc.view.ConferenceDataView;

/**
 * Created by xlagunas on 3/8/16.
 */
public class ConferenceActivity extends BaseActivity implements ConferenceDataView {
    private static final String EXTRA_ROOM_ID = "room_id";

    private static final String TAG = ConferenceActivity.class.getSimpleName();
    private static final int CONFERENCE_REQUEST_CODE = 200;

    private static final String[] permissions = {"android.permission.RECORD_AUDIO", "android.permission.CAMERA"};
    private VideoSource videoSource;

    @BindView(R.id.local_video_view)
    SurfaceViewRenderer localRenderer;
    CameraVideoCapturer videoCapturer;

    @BindView(R.id.remote_video_container)
    PercentRelativeLayout remoteRenderers;

    @Inject
    ConferencePresenter presenter;

    private boolean startedLocalStream = false;

    public static Intent startActivity(Context context, String roomId) {
        Intent intent = new Intent(context, ConferenceActivity.class);
        intent.putExtra(EXTRA_ROOM_ID, roomId);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_conference);

        String room = getIntent().getStringExtra(EXTRA_ROOM_ID);

        CustomApplication.getApp(this).getUserComponent().plus(new ConferenceModule(EglBase.create(), room)).inject(this);

        ButterKnife.bind(this);

        checkPermissions();
    }

    private void checkPermissions() {
        boolean arePermissionsOk = true;
        List<String> permissionsToAsk = null;

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                arePermissionsOk = false;
                if (permissionsToAsk == null) {
                    permissionsToAsk = new ArrayList<>();
                }
                permissionsToAsk.add(permissions[i]);
            }
        }

        if (arePermissionsOk) {
            init();
        } else {
            ActivityCompat.requestPermissions(this, permissionsToAsk.toArray(new String[permissionsToAsk.size()]), CONFERENCE_REQUEST_CODE);
        }
    }

    private void init() {
        presenter.setView(this);
        presenter.resume();
    }

    private CameraEnumerator getCameraEnumerator() {
        if (Build.VERSION.SDK_INT >= 21) {
            return new Camera2Enumerator(this);
        } else {
            return new Camera1Enumerator(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CONFERENCE_REQUEST_CODE) {
            boolean arePermissionsGranted = true;

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    arePermissionsGranted = false;
                }
            }
            if (arePermissionsGranted) {
                init();
            } else {
                Toast.makeText(this, "some permissions are not accepted, can't go on!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocalVideo();
    }

    private void initLocalVideo() {
        if (videoSource != null && !startedLocalStream) {

            startedLocalStream = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocalVideo();
        presenter.pause();

    }

    private void stopLocalVideo() {
        if (videoSource != null && startedLocalStream) {

            videoSource.stop();

            startedLocalStream = false;
        }
    }

    @Override
    protected void onDestroy() {
        if (localRenderer != null) {
            localRenderer.release();
            localRenderer = null;
        }
        if (videoCapturer != null) {
            videoCapturer.dispose();
            videoCapturer = null;
        }

        presenter.destroy();
        super.onDestroy();
    }

    @Override
    public void onLocalVideoGenerated(VideoSource videoSource) {
        this.videoSource = videoSource;
    }

    @Override
    public void onNewMediaStreamReceived(final String userId) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SurfaceViewRenderer renderer = new SurfaceViewRenderer(ConferenceActivity.this);
                renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
                remoteRenderers.addView(renderer);
                presenter.startRenderingVideo(userId, renderer);
            }
        });

    }

    @Override
    public void onIceCandidateGenerated(String userId, IceCandidate iceCandidate) {
        presenter.sendIceCandidate(userId, iceCandidate);
    }

    @Override
    public void onConnected(String userId) {
        Snackbar.make(findViewById(android.R.id.content), String.format("User %s connected", userId), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected(String userId) {
        Snackbar.make(findViewById(android.R.id.content), String.format("User %s disconnected", userId), Snackbar.LENGTH_SHORT).show();
        presenter.cleanConnection(userId);
    }

    @Override
    public void drainCandidates(String userId) {
        presenter.onFinishedGatheringIceCandidates(userId);
    }

    private void updateVideo() {
        int totalChilds = remoteRenderers.getChildCount();
        PercentLayoutHelper.PercentLayoutParams params;
        switch (totalChilds) {
            case 0:
                params = (PercentLayoutHelper.PercentLayoutParams) localRenderer.getLayoutParams();
                params.getPercentLayoutInfo().topMarginPercent = 0f;
                params.getPercentLayoutInfo().leftMarginPercent = 0f;
                params.getPercentLayoutInfo().widthPercent = 1f;
                params.getPercentLayoutInfo().heightPercent = 1f;
                localRenderer.requestLayout();
                break;
            case 1:
            case 2:
                for (int i = 0; i < totalChilds; i++) {
                    params = (PercentLayoutHelper.PercentLayoutParams) remoteRenderers.getChildAt(i).getLayoutParams();
                    params.getPercentLayoutInfo().widthPercent = 1f / totalChilds;
                    params.getPercentLayoutInfo().heightPercent = 1f;
                    params.getPercentLayoutInfo().leftMarginPercent = i * 0.5f;
                    Log.d(TAG, "Total width from child: " + i + " " + params.getPercentLayoutInfo().widthPercent);
                    remoteRenderers.getChildAt(i).requestLayout();
                }
                if (totalChilds == 1) {
                    params = (PercentLayoutHelper.PercentLayoutParams) localRenderer.getLayoutParams();
                    params.getPercentLayoutInfo().widthPercent = 0.3f;
                    params.getPercentLayoutInfo().heightPercent = 0.3f;
                    localRenderer.requestLayout();
                }
                break;
            case 3:
            case 4:
                for (int i = 0; i < totalChilds; i++) {
                    params = (PercentLayoutHelper.PercentLayoutParams) remoteRenderers.getChildAt(i).getLayoutParams();
                    params.getPercentLayoutInfo().widthPercent = 0.5f;
                    params.getPercentLayoutInfo().heightPercent = 0.5f;
                    //pull pair streams (aka 2 and 4 if it exists) to the right side of the screen
                    params.getPercentLayoutInfo().leftMarginPercent = (i % 2 == 0) ? 0f : 0.5f;
                    //pull down half the screen for the streams number 3 and 4
                    params.getPercentLayoutInfo().topMarginPercent = (i >= 2) ? 0.5f : 0f;
                    Log.d(TAG, "Total width from child: " + i + " " + params.getPercentLayoutInfo().widthPercent);
                    remoteRenderers.getChildAt(i).requestLayout();
                }
                if (totalChilds == 3) {
                    params = (PercentLayoutHelper.PercentLayoutParams) localRenderer.getLayoutParams();
                    params.getPercentLayoutInfo().topMarginPercent = 0.5f;
                    params.getPercentLayoutInfo().leftMarginPercent = 0.5f;
                    params.getPercentLayoutInfo().widthPercent = 0.5f;
                    params.getPercentLayoutInfo().heightPercent = 0.5f;
                    localRenderer.setVisibility(View.VISIBLE);
                } else {
                    localRenderer.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void updateLayout() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateVideo();
            }
        });
    }

    @Override
    public void startCameraStream() {
        createCapturer(getCameraEnumerator());
        presenter.initLocalSource(localRenderer, videoCapturer);
    }

    @Override
    public void removeRenderer(final SurfaceViewRenderer remoteRenderer) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = remoteRenderers.getChildCount() - 1; i >= 0; i--) {
                    View view = remoteRenderers.getChildAt(i);
                    if (remoteRenderer == view) {
                        Log.d(TAG, "Deleting stream");
                        remoteRenderers.removeView(remoteRenderers.getChildAt(i));
                        updateVideo();
                        return;
                    }
                    Log.d(TAG, "Stream not found");
                }
            }
        });
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

}
