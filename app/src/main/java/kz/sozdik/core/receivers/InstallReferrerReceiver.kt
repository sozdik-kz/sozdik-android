package kz.sozdik.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

private const val UTM_SOURCE_KEY = "utm_source"
private const val UTF_8 = "UTF-8"
private const val REFERRER_KEY = "referrer"
private const val INSTALL_FROM_EVENT_KEY = "install_from"

class InstallReferrerReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val referrer = intent.extras?.getString(REFERRER_KEY)
        Timber.d("Referrer is: $referrer")

        if (!referrer.isNullOrEmpty()) {
            val params = convertQueryToParams(referrer)
            val utmSource = params[UTM_SOURCE_KEY]
            if (utmSource != null) {
                val analytics = FirebaseAnalytics.getInstance(context)
                val bundle = Bundle()
                bundle.putString(UTM_SOURCE_KEY, utmSource)
                analytics.logEvent(INSTALL_FROM_EVENT_KEY, bundle)
            }
        }
    }

    private fun convertQueryToParams(query: String): Map<String, String> {
        val params = mutableMapOf<String, String>()
        val pairs = query.split("&")
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            try {
                params[URLDecoder.decode(pair.substring(0, idx), UTF_8)] =
                    URLDecoder.decode(pair.substring(idx + 1), UTF_8)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
        return params
    }
}