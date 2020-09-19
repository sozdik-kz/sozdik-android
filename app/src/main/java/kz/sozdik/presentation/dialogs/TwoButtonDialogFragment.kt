package kz.sozdik.presentation.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import kz.sozdik.R

const val ARG_TITLE = "ARG_TITLE"
const val ARG_MESSAGE = "ARG_MESSAGE"
const val ARG_REQUEST_CODE = "ARG_REQUEST_CODE"

class TwoButtonDialogFragment : DialogFragment() {

    companion object {
        fun create(title: String, message: String, requestCode: Int): TwoButtonDialogFragment =
            TwoButtonDialogFragment().apply {
                arguments = bundleOf(
                    ARG_TITLE to title,
                    ARG_MESSAGE to message,
                    ARG_REQUEST_CODE to requestCode
                )
            }
    }

    private var title: String? = null
    private var message: String? = null

    private var requestCode = 0

    private var listener: TwoButtonDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = arguments?.getString(ARG_TITLE)
        message = arguments?.getString(ARG_MESSAGE)
        requestCode = arguments?.getInt(ARG_REQUEST_CODE) ?: 0
        try {
            listener = parentFragment as TwoButtonDialogListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("$targetFragment must implement " + TwoButtonDialogListener::class.java.simpleName)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.yes) { _, _ -> listener?.onDialogPositiveClick(requestCode) }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                listener?.onDialogNegativeClick(requestCode)
            }
        return builder.create()
    }

    interface TwoButtonDialogListener {
        fun onDialogPositiveClick(requestCode: Int)

        fun onDialogNegativeClick(requestCode: Int)
    }
}