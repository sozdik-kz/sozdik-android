package kz.sozdik.presentation.utils

import kz.sozdik.BuildConfig

object DebugUtils {
    fun showDebugErrorMessage(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            throwable.printStackTrace()
            // Toast.makeText(App.getInstance(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}