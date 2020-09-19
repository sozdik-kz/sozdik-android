package kz.sozdik.favorites.presentation

import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.base.BaseMvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface FavoritesView : BaseMvpView {

    fun onSuccess(words: List<Word>)

    fun onError(message: String)

    fun showEmptyView(isVisible: Boolean)

    fun showLoadingProgress(isVisible: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showClearFavoriteWordsDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showUnfavoriteWordDialog()

    fun resetFavoritesList()
}