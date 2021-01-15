package kz.sozdik.profile.presentation

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import kotlinx.android.synthetic.main.fragment_settings.*
import kz.sozdik.R
import kz.sozdik.base.BaseActivity
import kz.sozdik.core.services.ClipboardService

private const val DRAW_OVERLAYS_SETTINGS_REQUEST_CODE = 1

class SettingsFragment :
    PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var dialogFragment: DialogFragment? = null

    private val languageKey: String by lazy { getString(R.string.pref_key_language) }
    private val quickTranslateKey: String by lazy { getString(R.string.pref_key_quick_translate) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager
            .getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)

        val showKazKeysPreference = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_show_kaz_keys))
        val vibratorPreference = findPreference<Preference>(getString(R.string.pref_turn_vibrator))
        vibratorPreference?.isEnabled = showKazKeysPreference?.isChecked == true
        showKazKeysPreference?.setOnPreferenceChangeListener { _, newValue ->
            vibratorPreference?.isEnabled = newValue as Boolean
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onPause() {
        super.onPause()
        dialogFragment?.dismiss()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (isDetached || !isAdded) {
            return
        }
        when (key) {
            languageKey -> {
                val language =
                    sharedPreferences.getString(languageKey, getString(R.string.settings_language_default_value))!!
                (activity as BaseActivity).setLanguage(language)
            }
            quickTranslateKey ->
                if (sharedPreferences.getBoolean(quickTranslateKey, false)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + context?.packageName)
                        )
                        startActivityForResult(intent, DRAW_OVERLAYS_SETTINGS_REQUEST_CODE)
                    } else {
                        startClipboardService()
                    }
                } else {
                    ClipboardService.stop(requireContext())
                }
        }
    }

    @Suppress("CollapsibleIfStatements")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED && requestCode == DRAW_OVERLAYS_SETTINGS_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(context)) {
                    val preference = findPreference<SwitchPreferenceCompat>(quickTranslateKey)
                    preference?.isChecked = false
                } else {
                    startClipboardService()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startClipboardService() {
        ClipboardService.start(requireContext())
        Toast.makeText(context, R.string.settings_quick_translation_toast, Toast.LENGTH_LONG).show()
    }
}