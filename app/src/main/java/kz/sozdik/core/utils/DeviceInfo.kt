package kz.sozdik.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import javax.inject.Inject

@SuppressLint("HardwareIds")
data class DeviceInfo @Inject constructor(val context: Context) {

    var deviceId: String

    init {
        val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        deviceId = if (id.isNullOrEmpty()) {
            "unknownDeviceId"
        } else {
            id.replace(" ", "")
        }
    }
}