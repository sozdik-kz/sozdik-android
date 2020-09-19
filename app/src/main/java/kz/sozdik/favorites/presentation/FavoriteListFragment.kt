package kz.sozdik.favorites.presentation

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.android.synthetic.main.view_loading.*
import kz.sozdik.R
import kz.sozdik.core.utils.Lang
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.favorites.di.DaggerFavoritesPresenterComponent
import kz.sozdik.history.presentation.WordsAdapter
import kz.sozdik.main.WordHandler
import kz.sozdik.presentation.dialogs.TwoButtonDialogFragment
import kz.sozdik.presentation.utils.showToast
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

private const val RC_REMOVE_WORD_FROM_FAVORITES = 1
private const val RC_CLEAR_FAVORITES = 2

private const val ARG_LANG_FROM = "ARG_LANG_FROM"

class FavoriteListFragment :
    MvpAppCompatFragment(R.layout.fragment_favorites),
    FavoritesView,
    TwoButtonDialogFragment.TwoButtonDialogListener {

    companion object {
        fun create(langFrom: String): FavoriteListFragment =
            FavoriteListFragment().apply {
                arguments = bundleOf(ARG_LANG_FROM to langFrom)
            }
    }

    @InjectPresenter
    lateinit var presenter: FavoritesPresenter

    @ProvidePresenter
    fun providePresenter(): FavoritesPresenter {
        val langFrom = arguments?.getString(ARG_LANG_FROM) ?: Lang.KAZAKH_CYRILLIC
        return DaggerFavoritesPresenterComponent.factory()
            .create(
                appDeps = requireContext().getAppDepsProvider(),
                langFrom = langFrom
            )
            .getFavoritesPresenter()
    }

    private var clearWordsDialog: DialogFragment? = null
    private val wordHandler by lazy { parentFragment?.parentFragment as WordHandler }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        favoritesRecyclerView.layoutManager = layoutManager
        favoritesRecyclerView.addItemDecoration(dividerItemDecoration)

        presenter.fetchFavorites()
    }

    override fun onPause() {
        super.onPause()
        clearWordsDialog?.dismiss()
    }

    override fun onDialogPositiveClick(requestCode: Int) {
        when (requestCode) {
            RC_CLEAR_FAVORITES -> presenter.clearFavoriteWords()
            RC_REMOVE_WORD_FROM_FAVORITES -> presenter.removeWordFromFavorites()
        }
    }

    override fun onDialogNegativeClick(requestCode: Int) {
        // do nothing
    }

    override fun onSuccess(words: List<Word>) {
        setupAdapter(words)
    }

    override fun onError(message: String) {
        showToast(message)
    }

    override fun showEmptyView(isVisible: Boolean) {
        viewEmpty.isVisible = isVisible
    }

    override fun showLoadingProgress(isVisible: Boolean) {
        viewLoading.isVisible = isVisible
    }

    override fun showClearFavoriteWordsDialog() {
        clearWordsDialog = TwoButtonDialogFragment.create(
            getString(R.string.removing),
            getString(R.string.favorites_question_removing),
            RC_CLEAR_FAVORITES
        )
        clearWordsDialog?.show(childFragmentManager, "TwoButtonDialogFragment")
    }

    override fun showUnfavoriteWordDialog() {
        clearWordsDialog = TwoButtonDialogFragment.create(
            getString(R.string.removing),
            getString(R.string.favorites_question_word_removing),
            RC_REMOVE_WORD_FROM_FAVORITES
        )
        clearWordsDialog?.show(childFragmentManager, "DeleteWordDialogFragment")
    }

    override fun resetFavoritesList() {
        // Fixme: don't create new adapter after words removing, set empty list and notify adapter
        setupAdapter(emptyList())
    }

    override fun showMessage(message: String) {
        showToast(message)
    }

    fun onMenuClearClick() {
        presenter.onClearFavoritesClick()
    }

    private fun setupAdapter(words: List<Word>) {
        favoritesRecyclerView.adapter = WordsAdapter(
            words,
            { wordHandler.showWordTranslation(it) },
            { presenter.onWordLongClick(it) }
        )
    }
}