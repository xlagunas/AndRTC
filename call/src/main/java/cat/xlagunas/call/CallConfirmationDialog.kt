package cat.xlagunas.call

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class CallConfirmationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle("Call confirmation")
            .setMessage("Do you want to call ${arguments?.getString("name")}?")
            .setPositiveButton("Yes") { _, _ -> run { Timber.d("Proceed to call!") } }
            .setNegativeButton("No") { _, _ -> dismiss() }
            .create()
    }

    companion object {
        fun newInstance(friendId: Long, fullname: String): CallConfirmationDialog {
            val frag = CallConfirmationDialog()
            val args = Bundle()
            args.putLong("id", friendId)
            args.putString("name", fullname)
            frag.arguments = args
            return frag
        }
    }
}

