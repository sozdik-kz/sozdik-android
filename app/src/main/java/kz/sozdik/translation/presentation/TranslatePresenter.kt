package kz.sozdik.translation.presentation

import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.core.system.PrefsManager
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.core.utils.Lang
import kz.sozdik.main.KeyboardState
import kz.sozdik.main.KeyboardState.CLOSED
import kz.sozdik.main.KeyboardState.OPEN
import kz.sozdik.presentation.utils.ErrorMessageFactory
import kz.sozdik.translation.domain.TranslateInteractor
import moxy.InjectViewState
import javax.inject.Inject

private const val VIBRATION_DURATION_MS = 25L

@InjectViewState
class TranslatePresenter @Inject constructor(
    private val translateInteractor: TranslateInteractor,
    private val resourceManager: ResourceManager,
    private val prefs: PrefsManager
) : BaseMvpPresenter<TranslateView>() {

    private var langFrom = Lang.RUSSIAN

    private var currentState: KeyboardState? = null

    fun translate(text: String) {
        if (text.isBlank()) {
            viewState.showMessage(resourceManager.getString(R.string.translate_error_empty_text))
            return
        }
        val langTo = if (langFrom == Lang.KAZAKH_CYRILLIC) Lang.RUSSIAN else Lang.KAZAKH_CYRILLIC
        launch {
            try {
                val translation = translateInteractor.translate(langFrom, langTo, text)
                viewState.onSuccess(translation)
            } catch (e: Throwable) {
                viewState.showMessage(ErrorMessageFactory.create(resourceManager, e))
            }
        }
    }

    fun toggleTranslationCourse() {
        when (langFrom) {
            Lang.KAZAKH_CYRILLIC -> {
                langFrom = Lang.RUSSIAN
                viewState.showKazakhLetters(false)
            }
            Lang.RUSSIAN -> {
                langFrom = Lang.KAZAKH_CYRILLIC
                showKazakhLettersIfAvailable()
            }
        }
        viewState.showTranslationCourse(langFrom)
    }

    fun onKeyboardStateChanged(newState: KeyboardState) {
        currentState = newState
        when (newState) {
            OPEN -> {
                viewState.showTranslationCourse(langFrom)
                viewState.onKeyboardOpened()
                showKazakhLettersIfAvailable()
            }
            CLOSED -> {
                viewState.onKeyboardClosed()
                viewState.showKazakhLetters(false)
            }
        }
    }

    private fun showKazakhLettersIfAvailable() {
        viewState.showKazakhLetters(isKazakhLettersAvailable() && currentState == OPEN)
    }

    private fun isKazakhLettersAvailable(): Boolean {
        val prefKey = resourceManager.getString(R.string.pref_show_kaz_keys)
        return langFrom == Lang.KAZAKH_CYRILLIC && prefs.getBoolean(prefKey, true)
    }

    fun onKazakhLetterClick() {
        if (Lang.RUSSIAN == langFrom) {
            toggleTranslationCourse()
        }

        val prefKey = resourceManager.getString(R.string.pref_turn_vibrator)
        if (prefs.getBoolean(prefKey, true)) {
            viewState.vibrate(VIBRATION_DURATION_MS)
        }
    }
}