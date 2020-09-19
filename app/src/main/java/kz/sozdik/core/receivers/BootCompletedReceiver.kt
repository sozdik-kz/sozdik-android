package kz.sozdik.core.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kz.sozdik.core.services.ClipboardService
import kz.sozdik.core.system.PrefsManager
import kz.sozdik.di.getAppDepsProvider
import javax.inject.Inject

class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var prefsManager: PrefsManager

    override fun onReceive(context: Context, intent: Intent) {
        injectDeps(context)
        if (intent.action == Intent.ACTION_BOOT_COMPLETED &&
            prefsManager.isQuickTranslateEnabled()
        ) {
            ClipboardService.start(context)
        }
    }

    private fun injectDeps(context: Context) {
        DaggerBootCompletedReceiverComponent.builder()
            .appDependency(context.getAppDepsProvider())
            .build()
            .inject(this)
    }
}