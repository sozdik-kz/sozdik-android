package kz.sozdik.history.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_history.historyRecyclerView
import kotlinx.android.synthetic.main.fragment_history.viewEmpty
import kotlinx.android.synthetic.main.view_loading.viewLoading
import kz.sozdik.R
import kz.sozdik.core.utils.Lang
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.history.di.DaggerHistoryPresenterComponent
import kz.sozdik.main.WordHandler
import kz.sozdik.presentation.dialogs.TwoButtonDialogFragment
import kz.sozdik.presentation.utils.showToast
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

private const val RC_REMOVE_WORD = 1
private const val RC_CLEAR_HISTORY = 2
private const val ARG_LANG_FROM = "ARG_LANG_FROM"

class HistoryListFragment :
    MvpAppCompatFragment(R.layout.fragment_history),
    HistoryView,
    TwoButtonDialogFragment.TwoButtonDialogListener {

    companion object {
        fun create(langFrom: String): HistoryListFragment =
            HistoryListFragment().apply {
                arguments = bundleOf(ARG_LANG_FROM to langFrom)
            }
    }

    @InjectPresenter
    lateinit var presenter: HistoryPresenter

    @ProvidePresenter
    fun providePresenter(): HistoryPresenter {
        val langFrom = arguments?.getString(ARG_LANG_FROM) ?: Lang.KAZAKH_CYRILLIC
        return DaggerHistoryPresenterComponent.factory()
            .create(
                appDeps = requireContext().getAppDepsProvider(),
                langFrom = langFrom
            )
            .getHistoryPresenter()
    }

    private var clearWordsDialog: DialogFragment? = null
    private val wordHandler by lazy { parentFragment?.parentFragment as WordHandler }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        historyRecyclerView.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        historyRecyclerView.addItemDecoration(dividerItemDecoration)

        presenter.fetchHistory()
    }

    override fun onPause() {
        super.onPause()
        clearWordsDialog?.dismiss()
    }

    override fun onDialogPositiveClick(requestCode: Int) {
        when (requestCode) {
            RC_REMOVE_WORD -> presenter.removeWord()
            RC_CLEAR_HISTORY -> presenter.clearHistory()
        }
    }

    override fun onDialogNegativeClick(requestCode: Int) {
        // do nothing
    }

    override fun onSuccess(words: List<Word>) {
        historyRecyclerView.adapter = WordsAdapter(
            words,
            { presenter.onWordSelected(it) },
            { presenter.onWordLongClick(it) }
        )
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

    override fun showWordTranslation(word: Word) {
        wordHandler.showWordTranslation(word)
    }

    override fun showClearHistoryDialog() {
        clearWordsDialog = TwoButtonDialogFragment.create(
            getString(R.string.removing),
            getString(R.string.history_question_removing),
            RC_CLEAR_HISTORY
        )
        clearWordsDialog?.show(childFragmentManager, "TwoButtonDialogFragment")
    }

    override fun showDeleteWordDialog() {
        clearWordsDialog = TwoButtonDialogFragment.create(
            getString(R.string.removing),
            getString(R.string.history_question_word_removing),
            RC_REMOVE_WORD
        )
        clearWordsDialog?.show(childFragmentManager, "DeleteWordDialogFragment")
    }

    override fun resetHistoryList() {
        // Fixme: don't create new adapter after words removing, set empty list and notify adapter
        historyRecyclerView.adapter = WordsAdapter(
            emptyList(),
            { presenter.onWordSelected(it) },
            { presenter.onWordLongClick(it) }
        )
        showEmptyView(true)
    }

    override fun showMessage(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun onMenuClearClick() {
        presenter.onClearHistoryClick()
    }
}