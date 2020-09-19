package kz.sozdik.history.presentation

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.history.domain.HistoryInteractor
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

private const val LOADING_DELAY_MS = 500L

@InjectViewState
class HistoryPresenter @Inject constructor(
    private val historyInteractor: HistoryInteractor,
    private val resourceManager: ResourceManager,
    private val langFrom: String
) : BaseMvpPresenter<HistoryView>() {

    private var wordToRemove: Word? = null

    fun onClearHistoryClick() {
        launch {
            if (historyInteractor.getWordsCount() == 0) {
                viewState.onError(resourceManager.getString(R.string.history_empty))
            } else {
                viewState.showClearHistoryDialog()
            }
        }
    }

    fun fetchHistory() {
        viewState.showEmptyView(false)
        viewState.showLoadingProgress(true)
        launch {
            try {
                delay(LOADING_DELAY_MS)
                val words = historyInteractor.getHistory(langFrom)
                viewState.onSuccess(words)
                if (words.isEmpty()) {
                    viewState.showEmptyView(true)
                }
            } catch (t: Throwable) {
                viewState.onError(t.localizedMessage)
            } finally {
                viewState.showLoadingProgress(false)
            }
        }
    }

    fun clearHistory() {
        launch {
            try {
                historyInteractor.clearHistory(langFrom)
                viewState.resetHistoryList()
            } catch (e: Throwable) {
                Timber.e(e, "Unable to clear history")
            }
        }
    }

    fun removeWord() {
        wordToRemove?.let {
            launch {
                try {
                    historyInteractor.removeWord(it)
                    fetchHistory()
                } catch (e: Throwable) {
                    Timber.e(e, "Unable to remove word ${it.phrase}")
                }
            }
        }
    }

    fun onWordSelected(word: Word) {
        viewState.showWordTranslation(word)
    }

    fun onWordLongClick(word: Word) {
        wordToRemove = word
        viewState.showDeleteWordDialog()
    }

    fun onLogout() {
        viewState.resetHistoryList()
    }
}