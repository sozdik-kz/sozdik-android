package kz.sozdik.register.presentation.registration

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

import kz.sozdik.R

class InfoDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.register_dialog_info_title)
            .setMessage(R.string.register_dialog_info_message)
            .setPositiveButton(R.string.dialog_ok, null)
        return builder.create()
    }
}