package kz.sozdik.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ShareCompat
import kz.sozdik.R

object AppUtils {

    fun sharePlainText(activity: Activity, textToShare: String) {
        ShareCompat.IntentBuilder
            .from(activity)
            .setText(textToShare)
            .setType("text/plain")
            .setChooserTitle(activity.getString(R.string.app_name))
            .startChooser()
    }

    fun getAppVersion(context: Context): String {
        var clientVersion = "0.00"
        try {
            clientVersion = context.packageManager.getPackageInfo(
                context.packageName, 0
            ).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return clientVersion
    }
}