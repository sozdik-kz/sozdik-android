package kz.sozdik.core.fcm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.logout.LogoutInteractor
import kz.sozdik.main.MainActivity
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val TYPE_LOGOUT = "logout"

class PushService : FirebaseMessagingService(), CoroutineScope {

    companion object {
        const val ACTION_LOGOUT = "kz.sozdik.core.fcm.FirebaseMessagingService.ACTION_LOGOUT"
    }

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Main.immediate

    @Inject
    lateinit var logoutInteractor: LogoutInteractor

    private val component: PushServiceComponent by lazy {
        DaggerPushServiceComponent.builder()
            .appDependency(getAppDepsProvider())
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
    }

    override fun onNewToken(token: String) {
        Timber.d("onNewToken() is called: $token")
        // TODO: Implement a method that will send token to the backend
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        when (remoteMessage.data["type"]) {
            TYPE_LOGOUT -> logout()
            else -> showNotification()
        }
    }

    private fun logout() {
        launch {
            try {
                logoutInteractor.logout()
                LocalBroadcastManager.getInstance(this@PushService)
                    .sendBroadcast(Intent(ACTION_LOGOUT))
            } catch (e: Throwable) {
                Timber.e(e, "Unable to logout")
            }
        }
    }

    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setColorized(true)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("")
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }
}