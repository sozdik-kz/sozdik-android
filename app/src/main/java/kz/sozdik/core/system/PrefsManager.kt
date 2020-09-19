package kz.sozdik.core.system

import android.content.SharedPreferences
import kz.sozdik.core.utils.Lang
import javax.inject.Inject

private const val TRANSLATION_COURSE_KEY = "pref_key_translation_course"
private const val QUICK_TRANSLATE_KEY = "pref_key_quick_translate"

@Suppress("TooManyFunctions")
class PrefsManager @Inject constructor(private val prefs: SharedPreferences) {

    fun isLatinModeEnabled(): Boolean = getBoolean("pref_key_kaz_roman", false)

    fun isKazakhLettersEnabled(): Boolean = getBoolean("pref_show_kaz_keys", true)

    fun isVibratorEnabled(): Boolean = getBoolean("pref_turn_vibrator", true)

    fun isNightModeEnabled(): Boolean = getBoolean("pref_key_night_mode_enabled", false)

    fun shouldCleanInputAfterTranslation(): Boolean =
        getBoolean("pref_key_clear_input_after_success_translation", true)

    fun setLanguageFrom(value: String) {
        put(TRANSLATION_COURSE_KEY, value)
    }

    fun getLanguageFrom(): String = getString(TRANSLATION_COURSE_KEY, Lang.RUSSIAN) ?: Lang.RUSSIAN

    fun isQuickTranslateEnabled(): Boolean = getBoolean(QUICK_TRANSLATE_KEY, false)

    fun setQuickTranslateEnabled(value: Boolean) {
        put(QUICK_TRANSLATE_KEY, value)
    }

    fun getString(key: String, default: String?): String? = prefs.getString(key, default)

    fun getBoolean(key: String, default: Boolean) = prefs.getBoolean(key, default)

    private fun put(key: String, value: String) {
        prefs.edit().apply {
            putString(key, value)
            apply()
        }
    }

    private fun put(key: String, value: Boolean) {
        prefs.edit().apply {
            putBoolean(key, value)
            apply()
        }
    }
}