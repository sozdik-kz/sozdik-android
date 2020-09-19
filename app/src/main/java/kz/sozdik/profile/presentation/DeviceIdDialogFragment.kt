package kz.sozdik.profile.presentation

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kz.sozdik.R
import kz.sozdik.core.utils.DeviceInfo
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.presentation.utils.showToast
import kz.sozdik.profile.di.DaggerProfileComponent
import kz.sozdik.profile.di.ProfileComponent
import javax.inject.Inject

class DeviceIdDialogFragment : DialogFragment() {
    @Inject
    lateinit var deviceInfo: DeviceInfo

    private val component: ProfileComponent by lazy {
        DaggerProfileComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_license_number)
            .setMessage(deviceInfo.deviceId)
            .setPositiveButton(R.string.close) { _, _ -> dismiss() }
            .setNeutralButton(R.string.copy_text) { _, _ ->
                val clipboard =
                    context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("deviceId", deviceInfo.deviceId)
                clipboard.setPrimaryClip(clipData)
                showToast(R.string.license_number_copied_to_buffer)
            }
            .create()
    }
}