package cat.xlagunas.conference.ui

import android.Manifest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.webrtc.MediaConstraints

class ConferencePermissionsTest {
    @Test
    fun givenCameraAndAudio_whenRequiredPermissions_thenCameraAndRecordAudio() {
        val result = requiredPermissions(constraints(video = true, audio = true))

        assertThat(result).containsExactly(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

    @Test
    fun givenCameraOnly_whenRequiredPermissions_thenCamera() {
        val result = requiredPermissions(constraints(video = true, audio = false))

        assertThat(result).containsExactly(Manifest.permission.CAMERA)
    }

    @Test
    fun givenAudioOnly_whenRequiredPermissions_thenRecordAudio() {
        val result = requiredPermissions(constraints(video = false, audio = true))

        assertThat(result).containsExactly(Manifest.permission.RECORD_AUDIO)
    }

    @Test
    fun givenNoMedia_whenRequiredPermissions_thenEmpty() {
        val result = requiredPermissions(constraints(video = false, audio = false))

        assertThat(result).isEmpty()
    }

    private fun constraints(
        video: Boolean,
        audio: Boolean,
    ) = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveAudio", audio.toString()))
        mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveVideo", video.toString()))
    }
}
