package kz.sozdik.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kz.sozdik.R
import kz.sozdik.base.BaseActivity
import kz.sozdik.core.services.ClipboardService
import kz.sozdik.core.system.PrefsManager
import kz.sozdik.core.utils.PreferencesHelper
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.dictionary.presentation.DictionaryFragment
import kz.sozdik.presentation.utils.popFragment
import kz.sozdik.presentation.utils.replaceFragment
import kz.sozdik.profile.domain.ProfileInteractor
import timber.log.Timber
import javax.inject.Inject

private const val DOUBLE_PRESS_BACK_BUTTON_DELAY_MS = 2000L

class MainActivity : BaseActivity() {

    companion object {
        const val ACTION_BUBBLE = "action_bubble"
        const val EXTRA_PHRASE = "extra_phrase"
    }

    private var phraseFromClipboard: String? = null

    private var doubleBackToExitPressedOnce = false
    private var exitToast: Toast? = null

    @Inject
    lateinit var prefsManager: PrefsManager

    @Inject
    lateinit var profileInteractor: ProfileInteractor

    override val layoutId = R.layout.activity_main

    private val component: MainActivityComponent by lazy {
        DaggerMainActivityComponent.builder()
            .appDependency(getAppDepsProvider())
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)

        component.inject(this)

        adView.initWidget(mvpDelegate)
        volumeControlStream = AudioManager.STREAM_MUSIC

        if (resources.getBoolean(R.bool.is_phone)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        if (savedInstanceState == null) {
            replaceFragment(MainFragment())
        }

        handleIntent(intent)

        startClipboardService()

        if (prefsManager.isQuickTranslateEnabled() &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !Settings.canDrawOverlays(this)
        ) {
            prefsManager.setQuickTranslateEnabled(false)
        }

        lifecycleScope.launchWhenCreated {
            try {
                profileInteractor.loadProfile()
            } catch (e: Throwable) {
                Timber.e(e, "Unable to load profile")
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == null) {
            return
        }
        var phrase: String? = null
        when (intent.action) {
            Intent.ACTION_PROCESS_TEXT ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    phrase = intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT)
                }
            ACTION_BUBBLE -> phrase = intent.getStringExtra(EXTRA_PHRASE)
        }
        if (!phrase.isNullOrBlank()) {
            phraseFromClipboard = phrase
//            dictionaryFragment?.searchPhrase(phraseFromClipboard)
        }
    }

    override fun onResume() {
        super.onResume()

        PreferencesHelper.loadSettings(this)
        val isAdDisabled = PreferencesHelper.isAdsDisabled()
        adView.setIsAdDisabled(isAdDisabled)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.fragments[0].childFragmentManager.fragments.isNotEmpty()) {
            (supportFragmentManager.fragments[0].childFragmentManager.fragments[0] as? DictionaryFragment)?.let {
                if (!it.isVisible) {
                    (supportFragmentManager.fragments[0] as BackPressListener).onBackPressed()
                    return
                }
            }
        }

        if (popFragment()) return

        if (doubleBackToExitPressedOnce) {
            exitToast?.cancel()
            finish()
            return
        }
        doubleBackToExitPressedOnce = true
        exitToast = Toast.makeText(this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT)
        exitToast?.show()
        Handler().postDelayed(
            { doubleBackToExitPressedOnce = false },
            DOUBLE_PRESS_BACK_BUTTON_DELAY_MS
        )
    }

    private fun startClipboardService() {
        if (prefsManager.isQuickTranslateEnabled()) {
            ClipboardService.start(this)
        }
    }

    interface BackPressListener {
        fun onBackPressed()
    }
}