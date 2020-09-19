package kz.sozdik.history.presentation

import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.base.BaseMvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface HistoryView : BaseMvpView {

    fun onSuccess(words: List<Word>)

    fun onError(message: String)

    fun showEmptyView(isVisible: Boolean)

    fun showLoadingProgress(isVisible: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showWordTranslation(word: Word)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showClearHistoryDialog()

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showDeleteWordDialog()

    fun resetHistoryList()
}