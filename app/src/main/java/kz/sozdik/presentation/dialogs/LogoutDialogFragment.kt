package kz.sozdik.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kz.sozdik.R

class LogoutDialogFragment : DialogFragment() {

    companion object {
        fun create(listener: LogoutDialogListener): LogoutDialogFragment =
            LogoutDialogFragment().apply {
                logoutDialogListener = listener
            }
    }

    private var logoutDialogListener: LogoutDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (logoutDialogListener == null) {
            throw NullPointerException("LogoutDialogListener can't be null")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.logout_dialog_title)
            .setMessage(R.string.logout_dialog_question)
            .setPositiveButton(R.string.yes) { _, _ -> logoutDialogListener?.onDialogPositiveClick() }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                logoutDialogListener?.onDialogNegativeClick()
            }
        return builder.create()
    }

    interface LogoutDialogListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }
}