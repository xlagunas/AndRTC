package cat.xlagunas.call

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import cat.xlagunas.core.di.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject
import timber.log.Timber

class CallConfirmationDialog : DialogFragment() {

    @Inject
    lateinit var factory: ViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as CallComponentProvider).provideCallComponent()
            .inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val viewModel = ViewModelProviders.of(this, factory)[CallViewModel::class.java]
        return MaterialAlertDialogBuilder(context)
            .setTitle("Call confirmation")
            .setMessage("Do you want to call ${arguments?.getString("name")}?")
            .setPositiveButton("Yes") { _, _ ->
                run {
                    Timber.d("Proceed to call!")
                    viewModel.createCall(listOf(arguments?.getLong("id")!!))
                }
            }
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
