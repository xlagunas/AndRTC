package cat.xlagunas.call

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.core.di.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber
import javax.inject.Inject

class CallConfirmationDialog : DialogFragment() {
    @Inject
    lateinit var factory: ViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as CallComponentProvider).provideCallComponent()
            .inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val viewModel = ViewModelProvider(this, factory)[CallViewModel::class.java]
        val dialog =
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.call_confirmation_title)
                .setPositiveButton(R.string.call_confirmation_yes, null)
                .setNegativeButton(R.string.call_confirmation_no) { _, _ -> dismiss() }
                .create()
        dialog.setOnShowListener {
            // Replace the default listener, which would dismiss the dialog on click.
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                Timber.d("Proceed to call!")
                viewModel.createCall(listOf(requireArguments().getLong("id")))
            }
            // Runs once per dialog instance, so the observer is registered only once.
            viewModel.state.observe(this) { state -> render(dialog, state) }
        }
        return dialog
    }

    private fun render(
        dialog: AlertDialog,
        state: CallState,
    ) {
        val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        when (state) {
            CallState.Idle -> {
                dialog.setMessage(
                    getString(R.string.call_confirmation_message, requireArguments().getString("name")),
                )
                positive.setText(R.string.call_confirmation_yes)
                positive.isEnabled = true
                negative.isEnabled = true
            }
            CallState.Creating -> {
                dialog.setMessage(getString(R.string.call_creating))
                positive.isEnabled = false
                negative.isEnabled = false
            }
            CallState.Error -> {
                dialog.setMessage(getString(R.string.call_error))
                positive.setText(R.string.call_retry)
                positive.isEnabled = true
                negative.isEnabled = true
            }
            // State may be delivered after onSaveInstanceState, so avoid the state-loss crash.
            CallState.Started -> dismissAllowingStateLoss()
        }
    }

    companion object {
        fun newInstance(
            friendId: Long,
            fullname: String,
        ): CallConfirmationDialog {
            val frag = CallConfirmationDialog()
            val args = Bundle()
            args.putLong("id", friendId)
            args.putString("name", fullname)
            frag.arguments = args
            return frag
        }
    }
}
