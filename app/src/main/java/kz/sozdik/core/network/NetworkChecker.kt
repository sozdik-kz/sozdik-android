package kz.sozdik.core.network

import android.content.Context
import android.net.ConnectivityManager

import javax.inject.Inject

data class NetworkChecker @Inject constructor(private val context: Context) {

    val isConnected: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
}