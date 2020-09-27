package kz.sozdik.dictionary.presentation

import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.widgets.KazCharsView
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.SkipStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface DictionaryView : MvpView {

    fun showWord(word: Word)

    fun showLoadingProgress(isVisible: Boolean)

    fun showEmptyView(isVisible: Boolean)

    fun showSuggestionsView(isVisible: Boolean)

    fun showTranslationView(isVisible: Boolean)

    fun showKazakhLetters(isVisible: Boolean)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun expandSearchView()

    fun collapseSearchView()

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showNoNetworkError()

    fun onSuggestionsLoaded(suggestions: List<String>)

    fun playSound(url: String)

    @StateStrategyType(value = SkipStrategy::class)
    fun showSearchMenuItem(isVisible: Boolean)

    fun showWordMenuItems(isVisible: Boolean)

    fun updateWordMenuItemsStates(word: Word)

    @StateStrategyType(value = SkipStrategy::class)
    fun openBrowser(url: String)

    @StateStrategyType(value = OneExecutionStateStrategy::class)
    fun showError(message: String)

    fun showTranslationCourse(language: String)

    fun showSearchButton()

    fun setSearchPhrase(phrase: String)

    fun switchTheme(isNightMode: Boolean)

    fun onFavoriteStateChanged(word: Word, isFavorite: Boolean)

    fun showShareScreen(word: Word)

    fun copyToClipboard(word: Word)

    fun setToolbarTitle(title: String)

    fun vibrate(milliseconds: Long)

    fun setKazakhCharsType(type: KazCharsView.Type)
}