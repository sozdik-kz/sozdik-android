package kz.sozdik.core.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.txusballesteros.bubbles.BubbleLayout
import com.txusballesteros.bubbles.BubblesManager
import kz.sozdik.R
import kz.sozdik.core.system.PrefsManager
import kz.sozdik.core.utils.InternetConnectionHelper
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.main.MainActivity
import pl.bclogic.pulsator4droid.library.PulsatorLayout
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

private const val BUBBLE_SHOW_TIME: Long = 7000
private const val CHANNEL_ID = "Быстрый перевод"

private const val BUBBLE_HORIZONTAL_MARGIN = 60

class ClipboardService : Service(), ClipboardManager.OnPrimaryClipChangedListener {

    companion object {
        fun start(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !Settings.canDrawOverlays(context)
            ) {
                Timber.w("Permission denied to draw overlays")
                return
            }
            val intent = Intent(context, ClipboardService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, ClipboardService::class.java))
        }
    }

    private val validLettersRegex = "[а-яА-ЯәөқғіһңұүӘӨҚҒІҺҢҰҮ]+".toRegex()

    private var timer: Timer? = null
    private var clipBoardManager: ClipboardManager? = null
    private var copiedText: String? = null
    private var bubblesManager: BubblesManager? = null
    private var bubbleView: BubbleLayout? = null

    @Inject
    lateinit var prefsManager: PrefsManager

    private val component: ClipBoardServiceComponent by lazy {
        DaggerClipBoardServiceComponent.builder()
            .appDependency(getAppDepsProvider())
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        component.inject(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            configureOreoNotification()
        }

        bubblesManager = BubblesManager.Builder(this).build()
        bubblesManager?.initialize()

        if (clipBoardManager == null) {
            clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipBoardManager?.addPrimaryClipChangedListener(this)
        }
    }

    override fun onDestroy() {
        bubblesManager?.recycle()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    @Suppress("ReturnCount")
    override fun onPrimaryClipChanged() {
        if (!prefsManager.isQuickTranslateEnabled()) {
            return
        }

        val context = this@ClipboardService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            return
        }

        if (!InternetConnectionHelper.isOnline(context)) {
            return
        }

        val text = getTextFromClipboard()?.trim()
        // Проверяем, т.к. onPrimaryClipChanged() вызывается несколько раз с одним и тем же текстом
        if (text.isNullOrBlank() || text == copiedText) {
            return
        }

        // Если словосочетание или предложение, то не показываем Bubble кнопку
        if (text.contains(" ")) {
            return
        }

        // Если не содержит казахские и/или русские буквы, то не показываем Bubble кнопку
        if (!containsValidLetters(text)) {
            return
        }

        copiedText = text

        if (bubbleView != null) {
            bubblesManager?.removeBubble(bubbleView)
        }
        bubbleView = LayoutInflater.from(applicationContext)
            .inflate(R.layout.bubble_layout, null) as BubbleLayout
        bubbleView?.setOnBubbleClickListener {
            startActivity(
                Intent(context, MainActivity::class.java)
                    .putExtra(MainActivity.EXTRA_PHRASE, copiedText)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setAction(MainActivity.ACTION_BUBBLE)
            )

            bubblesManager?.removeBubble(bubbleView)
            bubbleView = null
            copiedText = null
        }

        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        windowManager.defaultDisplay.run {
            getSize(size)
        }

        bubbleView?.animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
        bubblesManager?.addBubble(bubbleView, size.x - BUBBLE_HORIZONTAL_MARGIN, size.y / 2)

        val pulsator = bubbleView?.findViewById<PulsatorLayout>(R.id.pulsator)
        pulsator?.start()

        timer?.cancel()
        timer = Timer()
        timer?.schedule(
            object : TimerTask() {
                override fun run() {
                    if (bubbleView != null) {
                        bubblesManager?.removeBubble(bubbleView)
                        bubbleView = null
                        copiedText = null
                    }
                }
            },
            BUBBLE_SHOW_TIME
        )
    }

    private fun getTextFromClipboard(): String? {
        val clipData = clipBoardManager?.primaryClip
        val firstItem = clipData?.getItemAt(0)
        val copiedFromThisApp = applicationContext.packageName == clipData?.description?.label
        return if (firstItem?.text == null || copiedFromThisApp) {
            null
        } else {
            firstItem.text.toString()
        }
    }

    private fun containsValidLetters(name: String): Boolean = name.matches(validLettersRegex)

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun configureOreoNotification() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.settings_quick_translation),
            NotificationManager.IMPORTANCE_LOW
        )
        channel.setShowBadge(false)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
            .build()

        startForeground(1, notification)
    }
}