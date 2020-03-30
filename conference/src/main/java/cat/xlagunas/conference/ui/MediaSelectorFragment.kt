package cat.xlagunas.conference.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.webrtc.MediaConstraints

class MediaSelectorFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val listView = ListView(this.context)
        listView.adapter = ArrayAdapter<String>(
            this.context,
            android.R.layout.simple_list_item_1,
            arrayOf("Camera and audio", "Camera only", "Audio only")
        )
        val viewmodel = ViewModelProviders.of(requireActivity()).get(ConferenceViewModel::class.java)

        listView.setOnItemClickListener { _, _, position, _ ->
            val selection = when (position) {
                0 -> generatePeerConnectionConstraints(video = true, audio = true)
                1 -> generatePeerConnectionConstraints(video = true, audio = false)
                2 -> generatePeerConnectionConstraints(video = false, audio = true)
                else -> generatePeerConnectionConstraints(video = false, audio = false)
            }
            viewmodel.requestedMedia.postValue(selection)
            dismiss()
        }
        return listView
    }

    private fun generatePeerConnectionConstraints(video: Boolean, audio: Boolean) =
        MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveAudio", audio.toString()))
            mandatory.add(MediaConstraints.KeyValuePair("offerToReceiveVideo", video.toString()))
        }
}