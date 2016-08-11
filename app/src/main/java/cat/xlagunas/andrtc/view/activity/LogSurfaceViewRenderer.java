package cat.xlagunas.andrtc.view.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

/**
 * Created by xlagunas on 3/8/16.
 */
public class LogSurfaceViewRenderer extends SurfaceViewRenderer {
    public LogSurfaceViewRenderer(Context context) {
        super(context);
    }

    public LogSurfaceViewRenderer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void renderFrame(VideoRenderer.I420Frame frame) {
        Log.d("LogSurfaceViewRender", "rendering frame");
        super.renderFrame(frame);
    }
}