package kz.sozdik.dictionary.presentation

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.core.exceptions.NoNetworkException
import kz.sozdik.core.system.PrefsManager
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.core.utils.Lang
import kz.sozdik.dictionary.domain.DictionaryInteractor
import kz.sozdik.dictionary.domain.model.SuggestionsResult.NoSuggestions
import kz.sozdik.dictionary.domain.model.SuggestionsResult.Success
import kz.sozdik.dictionary.domain.model.SuggestionsResult.WrongPhrase
import kz.sozdik.dictionary.domain.model.TranslatePhraseResult
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.favorites.domain.FavoritesInteractor
import kz.sozdik.main.KeyboardState
import kz.sozdik.main.KeyboardState.CLOSED
import kz.sozdik.main.KeyboardState.OPEN
import kz.sozdik.presentation.utils.DebugUtils
import kz.sozdik.presentation.utils.ErrorMessageFactory
import kz.sozdik.presentation.utils.PhraseUtils
import kz.sozdik.widgets.KazCharsView
import moxy.InjectViewState
import timber.log.Timber
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.Locale
import javax.inject.Inject

private const val LANG_FROM_URL_POSITION = 5
private const val LANG_TO_URL_POSITION = 6
private const val PHRASE_URL_POSITION = 7

private const val VIBRATION_DURATION_MS = 25L

private const val SUGGESTIONS_LOADING_DELAY_IN_MILLIS = 250L

@InjectViewState
class DictionaryPresenter @Inject constructor(
    private val dictionaryInteractor: DictionaryInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val resourceManager: ResourceManager,
    private val prefsManager: PrefsManager
) : BaseMvpPresenter<DictionaryView>() {

    private var currentWord: Word? = null
    private var suggestions: List<String>? = null

    // TODO: Avoid isTranslateFromHistory variable
    // This variable need only for proper show/hide empty view
    private var isTranslateFromHistory = false

    private var isTranslateRunning = false
    private var langFrom = Lang.RUSSIAN
    private var langTo = Lang.KAZAKH_CYRILLIC
    private var currentPhrase = ""
    private var currentKeyboardState = CLOSED

    private var loadSuggestionsJob: Job? = null

    // TODO: Should be private
    val isNightMode: Boolean = prefsManager.isNightModeEnabled()
    private val needClearInput = prefsManager.shouldCleanInputAfterTranslation()
    private val isLatinModeEnabled = prefsManager.isLatinModeEnabled()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        viewState.switchTheme(isNightMode)

        viewState.setKazakhCharsType(
            if (isLatinModeEnabled) {
                KazCharsView.Type.LATIN
            } else {
                KazCharsView.Type.CYRILLIC
            }
        )

        langFrom = prefsManager.getLanguageFrom()
        if (langFrom == Lang.KAZAKH_CYRILLIC && isLatinModeEnabled) {
            langFrom = Lang.KAZAKH_ROMAN
        } else if (langFrom == Lang.KAZAKH_ROMAN && !isLatinModeEnabled) {
            langFrom = Lang.KAZAKH_CYRILLIC
        }
        langTo = getLangTo(isLatinModeEnabled)

        setTranslationCourse(langFrom)

        updateToolbarTitle()
    }

    fun translatePhraseFromHistory(phrase: String) {
        isTranslateFromHistory = true
        translatePhrase(phrase)
    }

    fun translatePhrase(phrase: String) {
        viewState.showLoadingProgress(true)
        viewState.showEmptyView(false)
        viewState.showSuggestionsView(false)
        viewState.showTranslationView(false)

        val normalizedPhrase = PhraseUtils.normalize(phrase)

        launch {
            try {
                isTranslateRunning = true
                val result = dictionaryInteractor.translate(langFrom, langTo, normalizedPhrase)
                when (result) {
                    is TranslatePhraseResult.Success -> {
                        isTranslateFromHistory = false
                        isTranslateRunning = false
                        viewState.showLoadingProgress(false)
                        viewState.showTranslationView(true)
                        viewState.showTranslationCourse(result.word.langFrom)
                        viewState.showWord(result.word)
                        viewState.collapseSearchView()
                    }
                    TranslatePhraseResult.WrongPhrase -> {
                        viewState.showNoNetworkError()
                        viewState.showTranslationView(true)
                    }
                }
            } catch (t: Throwable) {
                isTranslateFromHistory = false
                isTranslateRunning = false
                viewState.showLoadingProgress(false)
                when (t) {
                    is NoNetworkException -> {
                        viewState.showNoNetworkError()
                        viewState.showTranslationView(true)
                    }
                    else -> DebugUtils.showDebugErrorMessage(t)
                }
                toggleEmptyView()
            }
        }
    }

    fun loadSuggestions(phrase: String) {
        currentPhrase = PhraseUtils.normalize(phrase)
        if (!Lang.isKazakh(langFrom) && isContainsKazakhLetters(currentPhrase)) {
            toggleTranslationCourse()
        }

        loadSuggestionsJob?.cancel()
        loadSuggestionsJob = launch {
            try {
                delay(SUGGESTIONS_LOADING_DELAY_IN_MILLIS)
                val result = dictionaryInteractor.getSuggestions(langFrom, langTo, currentPhrase)
                viewState.showSuggestionsView(result is Success)
                when (result) {
                    is Success -> viewState.onSuggestionsLoaded(result.suggestions)
                    NoSuggestions -> viewState.showError(
                        resourceManager.getString(R.string.dictionary_suggestions_not_found)
                    )
                    WrongPhrase ->
                        viewState.showError(resourceManager.getString(R.string.dictionary_wrong_phrase))
                }
                viewState.showLoadingProgress(false)
                viewState.showTranslationView(false)
            } catch (e: Throwable) {
                viewState.showLoadingProgress(false)
                when (e) {
                    is NoNetworkException -> viewState.showNoNetworkError()
                    else -> DebugUtils.showDebugErrorMessage(e)
                }
            }
        }
    }

    fun onSearchInputClear() {
        currentPhrase = ""
        suggestions = null
        viewState.showSuggestionsView(false)
        toggleEmptyView()
    }

    fun playSound() {
        val audioHash = currentWord?.audioHash
        if (audioHash.isNullOrEmpty()) {
            viewState.showError(resourceManager.getString(R.string.word_has_no_sound))
        } else {
            launch {
                try {
                    val audio = dictionaryInteractor.getAudio(audioHash)
                    viewState.playSound(audio.url)
                } catch (e: Throwable) {
                    Timber.e(e, "Unable to play audio")
                    viewState.showError(ErrorMessageFactory.create(resourceManager, e))
                }
            }
        }
    }

    fun viewPagerPageChanged(word: Word) {
        currentWord = word
        Timber.d(currentWord?.phrase)
        if (currentWord?.isRight == true) {
            viewState.showSearchMenuItem(false)
            showWordMenuItems(true, currentWord)
            viewState.updateWordMenuItemsStates(currentWord!!)
        } else {
            viewState.showSearchMenuItem(true)
            showWordMenuItems(false, currentWord)
        }
        langFrom = if (Lang.isKazakh(currentWord!!.langFrom)) {
            Lang.KAZAKH_CYRILLIC
        } else {
            Lang.RUSSIAN
        }
        viewState.showTranslationCourse(langFrom)
        updateToolbarTitle()
        viewState.showSearchButton()
    }

    fun searchViewExpanded() {
        if (currentWord != null && !needClearInput) {
            viewState.setSearchPhrase(currentWord!!.phrase)
        }
        showWordMenuItems(false, currentWord)
    }

    private fun updateToolbarTitle() {
        if (currentWord == null) {
            return
        }
        viewState.setToolbarTitle(
            if (needClearInput) {
                resourceManager.getString(R.string.tab_dictionary)
            } else {
                currentWord!!.phrase
            }
        )
    }

    fun searchViewCollapsed(word: Word?) {
        if (word != null) {
            showWordMenuItems(true, word)
            viewState.updateWordMenuItemsStates(word)
            viewState.showTranslationView(true)
        }
        toggleEmptyView()
    }

    private fun showWordMenuItems(isVisible: Boolean, word: Word?) {
        val needShowMenuItems = isVisible && word!!.isRight
        viewState.showWordMenuItems(needShowMenuItems)
    }

    fun onWebViewUrlClicked(url: String) {
        if (url.startsWith("http") || url.startsWith("www.")) {
            viewState.openBrowser(url)
            return
        }
        // Example of array:
        // index -> |     5     |     6     |    7   |
        // value -> | lang from |  lang to  | phrase |
        val values = url.split("/").dropLastWhile { it.isEmpty() }
        langFrom = values[LANG_FROM_URL_POSITION]
        langTo = values[LANG_TO_URL_POSITION]
        try {
            val phrase = URLDecoder.decode(values[PHRASE_URL_POSITION], "UTF-8")
            translatePhrase(phrase)
        } catch (e: UnsupportedEncodingException) {
            viewState.showError(resourceManager.getString(R.string.phrase_contains_invalid_characters))
        } catch (e: IllegalArgumentException) {
            viewState.showError(resourceManager.getString(R.string.phrase_contains_invalid_characters))
        }
    }

    fun onFabClicked() {
        if (currentKeyboardState == CLOSED) {
            viewState.expandSearchView()
        } else {
            onChangeTranslationCourseClicked()
        }
    }

    private fun setTranslationCourse(newLangFrom: String) {
        @Suppress("ComplexCondition")
        if (newLangFrom == Lang.KAZAKH_CYRILLIC && isLatinModeEnabled ||
            newLangFrom == Lang.KAZAKH_ROMAN && !isLatinModeEnabled
        ) {
            langFrom = newLangFrom
            langTo = getLangTo(isLatinModeEnabled)
            return
        }
        if (langFrom != newLangFrom) {
            onChangeTranslationCourseClicked()
        }
    }

    private fun onChangeTranslationCourseClicked() {
        toggleTranslationCourse()
        if (currentPhrase.isNotEmpty()) {
            loadSuggestions(currentPhrase)
        }
    }

    private fun toggleTranslationCourse() {
        if (Lang.isKazakh(langFrom)) {
            langFrom = Lang.RUSSIAN
            viewState.showKazakhLetters(false)
        } else {
            langFrom = if (isLatinModeEnabled) {
                Lang.KAZAKH_ROMAN
            } else {
                Lang.KAZAKH_CYRILLIC
            }
            showKazakhLettersIfAvailable()
        }
        langTo = getLangTo(isLatinModeEnabled)
        viewState.showTranslationCourse(langFrom)

        prefsManager.setLanguageFrom(langFrom)
    }

    private fun getLangTo(isLatinModeEnabled: Boolean): String =
        when {
            langFrom == Lang.RUSSIAN && isLatinModeEnabled -> Lang.KAZAKH_ROMAN
            langFrom == Lang.RUSSIAN && !isLatinModeEnabled -> Lang.KAZAKH_CYRILLIC
            else -> Lang.RUSSIAN
        }

    fun onKeyboardStateChanged(newState: KeyboardState) {
        when (newState) {
            OPEN -> {
                viewState.showTranslationCourse(langFrom)
                showKazakhLettersIfAvailable()
            }
            CLOSED -> {
                viewState.showKazakhLetters(false)
                viewState.showSearchButton()
            }
        }
        currentKeyboardState = newState
        toggleEmptyView()
    }

    fun onShareWordClick() {
        viewState.showShareScreen(currentWord!!)
    }

    fun onCopyToClipboardClick() {
        viewState.copyToClipboard(currentWord!!)
    }

    private fun showKazakhLettersIfAvailable() {
        if (Lang.isKazakh(langFrom) && prefsManager.isKazakhLettersEnabled()) {
            viewState.showKazakhLetters(true)
        }
    }

    fun onFavoriteClick() {
        currentWord?.let { word ->
            if (word.isFavorite) {
                unfavoriteWord(word)
            } else {
                favoriteWord(word)
            }
        }
    }

    private fun favoriteWord(word: Word) {
        launch {
            try {
                val word = favoritesInteractor.createFavoritesPhrase(word)
                if (word.isSameWord(currentWord)) {
                    currentWord?.favourite = 1
                }
                viewState.onFavoriteStateChanged(word, true)
            } catch (t: Throwable) {
                viewState.showError(ErrorMessageFactory.create(resourceManager, t))
            }
        }
    }

    private fun unfavoriteWord(word: Word) {
        launch {
            try {
                val word = favoritesInteractor.deletePhraseFromFavorites(word)
                if (word.isSameWord(currentWord)) {
                    currentWord?.favourite = 0
                }
                viewState.onFavoriteStateChanged(word, false)
            } catch (t: Throwable) {
                viewState.showError(ErrorMessageFactory.create(resourceManager, t))
            }
        }
    }

    fun onCreateOptionsMenu() {
        currentWord?.let { word ->
            if (word.isRight) {
                viewState.showSearchMenuItem(false)
                showWordMenuItems(true, word)
                viewState.updateWordMenuItemsStates(word)
            } else {
                viewState.showSearchMenuItem(true)
                showWordMenuItems(false, word)
            }
        }
    }

    fun onKazakhLetterClick() {
        if (!Lang.isKazakh(langFrom)) {
            toggleTranslationCourse()
        }

        if (prefsManager.isVibratorEnabled()) {
            viewState.vibrate(VIBRATION_DURATION_MS)
        }
    }

    private fun toggleEmptyView() {
        viewState.showEmptyView(needShowEmptyView())
    }

    private fun needShowEmptyView(): Boolean =
        currentKeyboardState == CLOSED &&
            !isTranslateRunning &&
            !isTranslateFromHistory &&
            currentWord == null &&
            (suggestions == null || suggestions!!.isEmpty())

    companion object {

        private val kazakhLetterList = arrayOf("ә", "ө", "қ", "ғ", "і", "һ", "ң", "ұ", "ү")

        /**
         * Returns true if and only if this string contains the kazakh char.
         *
         * @param inputString the sequence to search for
         * @return true if this string contains kazakh letter, false otherwise
         */
        private fun isContainsKazakhLetters(inputString: String): Boolean {
            val query = inputString.toLowerCase(Locale.ROOT)
            for (item in kazakhLetterList) {
                if (query.contains(item)) {
                    return true
                }
            }
            return false
        }
    }
}