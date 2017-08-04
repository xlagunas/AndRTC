package cat.xlagunas.andrtc.data.net.webrtc;

import android.media.AudioManager;

import javax.inject.Inject;

/**
 * Created by xlagunas on 11/8/16.
 */
public class WebRTCAudioManagerImpl implements WebRTCAudioManager {

    private static final String TAG = WebRTCAudioManagerImpl.class.getSimpleName();
    private final AudioManager audioManager;

    @Inject
    public WebRTCAudioManagerImpl(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    @Override
    public void init() {
        audioManager.requestAudioFocus(null,AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        audioManager.setSpeakerphoneOn(true);
    }

    @Override
    public void stop(){
        audioManager.abandonAudioFocus(null);
    }
}
