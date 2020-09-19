package kz.sozdik.favorites.presentation

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.favorites.domain.FavoritesInteractor
import kz.sozdik.presentation.utils.ErrorMessageFactory
import moxy.InjectViewState
import javax.inject.Inject

private const val LOADING_DELAY_MS = 500L

@InjectViewState
class FavoritesPresenter @Inject constructor(
    private val favoritesInteractor: FavoritesInteractor,
    private val resourceManager: ResourceManager,
    private val langFrom: String
) : BaseMvpPresenter<FavoritesView>() {

    private var wordToUnfavorite: Word? = null

    fun onClearFavoritesClick() {
        launch {
            if (favoritesInteractor.getFavoriteWordsCount() == 0) {
                viewState.onError(resourceManager.getString(R.string.favorites_is_empty))
            } else {
                viewState.showClearFavoriteWordsDialog()
            }
        }
    }

    fun onWordLongClick(word: Word) {
        wordToUnfavorite = word
        viewState.showUnfavoriteWordDialog()
    }

    fun clearFavoriteWords() {
        launch {
            try {
                favoritesInteractor.clearFavorites(langFrom)
                viewState.resetFavoritesList()
                viewState.showEmptyView(true)
                viewState.showMessage(resourceManager.getString(R.string.favorites_cleared))
            } catch (e: Throwable) {
                viewState.showLoadingProgress(false)
                viewState.onError(ErrorMessageFactory.create(resourceManager, e))
            }
        }
    }

    fun removeWordFromFavorites() {
        wordToUnfavorite?.let {
            launch {
                try {
                    val word = favoritesInteractor.deletePhraseFromFavorites(it)
                    fetchFavorites()
                } catch (t: Throwable) {
                    viewState.showLoadingProgress(false)
                    viewState.onError(ErrorMessageFactory.create(resourceManager, t))
                }
            }
        }
    }

    fun fetchFavorites() {
        viewState.showEmptyView(false)
        viewState.showLoadingProgress(true)

        launch {
            try {
                delay(LOADING_DELAY_MS)
                val words = favoritesInteractor.getFavorites(langFrom)
                viewState.showLoadingProgress(false)
                viewState.onSuccess(words)
                if (words.isEmpty()) {
                    viewState.showEmptyView(true)
                }
            } catch (t: Throwable) {
                viewState.showLoadingProgress(false)
                viewState.onError(ErrorMessageFactory.create(resourceManager, t))
            }
        }
    }

    fun onLogout() {
        viewState.resetFavoritesList()
        viewState.showEmptyView(true)
    }
}