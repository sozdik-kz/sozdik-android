package kz.sozdik.dictionary.presentation

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.fragment_dictionary.*
import kz.sozdik.R
import kz.sozdik.core.system.PrefsManager
import kz.sozdik.core.utils.Lang
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.dictionary.di.DaggerDictionaryPresenterComponent
import kz.sozdik.dictionary.di.DictionaryPresenterComponent
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.dictionary.widget.ObservableWebView
import kz.sozdik.main.KeyboardState
import kz.sozdik.main.KeyboardStateListener
import kz.sozdik.presentation.utils.showKeyboard
import kz.sozdik.presentation.utils.showToast
import kz.sozdik.widgets.KazCharsView
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

private const val TEXT_PLAIN_TYPE = "text/plain"
private const val BASE_WEB_VIEW_URL = "local://sozdik.kz/"
private const val WEB_VIEW_MIME_TYPE = "text/html"
private const val WEB_VIEW_ENCODING = "UTF-8"

private const val MAX_VIEW_PAGER_PAGES = 10

class DictionaryFragment :
    MvpAppCompatFragment(R.layout.fragment_dictionary),
    AdapterView.OnItemClickListener,
    KeyboardStateListener,
    DictionaryView {

    companion object {
        const val ARGUMENT_WORD = "argument_word"

        fun create(word: Word? = null): DictionaryFragment =
            DictionaryFragment().apply {
                if (word != null) {
                    arguments = bundleOf(ARGUMENT_WORD to word)
                }
            }
    }

    private var mainPagerAdapter = MainPagerAdapter()

    private var searchView: SearchView? = null
    private var etFindWord: AutoCompleteTextView? = null
    private var itemSuggestionLayoutId: Int = 0

    private var phraseFromClipboard: String? = null

    private val fontSize: Int by lazy {
        val size = prefsManager.getString(
            getString(R.string.pref_key_font_size),
            getString(R.string.font_size_default_value)
        )!!
        Integer.parseInt(size)
    }

    private val component: DictionaryPresenterComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerDictionaryPresenterComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
    }

    @InjectPresenter
    lateinit var presenter: DictionaryPresenter

    @ProvidePresenter
    fun providePresenter(): DictionaryPresenter = component.getDictionaryPresenter()

    @Inject
    lateinit var prefsManager: PrefsManager

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var vibrator: Vibrator

    private val currentWord: Word?
        get() = if (viewPager == null) {
            null
        } else {
            mainPagerAdapter.getWord(viewPager.currentItem)
        }

    // TODO: Move to presenter
    private val css: String
        get() {
            val cssResId = if (presenter.isNightMode) R.string.css_dark else R.string.css_light
            return getString(cssResId, fontSize, fontSize + 2)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val wordToDisplay = arguments?.getSerializable(ARGUMENT_WORD) as? Word
        if (wordToDisplay != null) {
            presenter.translatePhraseFromHistory(wordToDisplay)
        }
        setupViews()
        setupToolbar()
        setupMenu()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.adapter = null
    }

    override fun onDetach() {
        super.onDetach()
        searchView?.setOnQueryTextListener(null)
    }

    override fun expandSearchView() {
        val searchItem = toolbar.menu.findItem(R.id.action_search)
        if (!searchItem.isActionViewExpanded) {
            searchItem.expandActionView()
        } else {
            etFindWord?.showKeyboard()
        }
    }

    fun setPhraseFromClipboard(phrase: String?) {
        phraseFromClipboard = phrase
    }

    // -----------------------------------------------------------------------------
    // Here's what the app should do to add a view to the ViewPager.
    private fun addView(newPage: View, word: Word) {
        val pageIndex = mainPagerAdapter.addView(newPage, word)
        mainPagerAdapter.notifyDataSetChanged()
        // You might want to make "newPage" the currently displayed page:
        viewPager.setCurrentItem(pageIndex, true)

        if (pageIndex == 0) {
            presenter.viewPagerPageChanged(word)
        }
    }

    // -----------------------------------------------------------------------------
    // Here's what the app should do to remove a view from the ViewPager.
    private fun removeView(defunctPage: View) {
        var pageIndex = mainPagerAdapter.removeView(viewPager, defunctPage)
        // You might want to choose what page to display, if the current page was "defunctPage".
        if (pageIndex == mainPagerAdapter.count) {
            pageIndex--
        }
        viewPager.currentItem = pageIndex
    }

    private fun searchSuggestions(phrase: String) {
        if (phrase.isEmpty()) {
            presenter.onSearchInputClear()
            suggestionsListView.adapter = ArrayAdapter<Word>(requireContext(), android.R.layout.simple_list_item_1)
        } else {
            presenter.loadSuggestions(phrase)
        }
    }

    fun searchPhrase(phrase: String?) {
        phrase?.let {
            presenter.translatePhrase(it)
        }
    }

    override fun showWord(word: Word) {
        val wordPositionInAdapter = mainPagerAdapter.getWordPosition(word)
        if (wordPositionInAdapter != -1) {
            viewPager.setCurrentItem(wordPositionInAdapter, true)
            suggestionsListView.isVisible = false
            viewPagerContainer.isVisible = true
            collapseSearchView()
            return
        }
        if (mainPagerAdapter.count > 0 &&
            !mainPagerAdapter.getWord(mainPagerAdapter.count - 1)!!.isRight
        ) {
            removeView(mainPagerAdapter.getView(mainPagerAdapter.count - 1))
        }
        if (MAX_VIEW_PAGER_PAGES == mainPagerAdapter.count) {
            removeView(mainPagerAdapter.getView(0))
        }

        setTranslationContent(word, createWebView(word))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun createWebView(word: Word): WebView {
        return (layoutInflater.inflate(R.layout.web_view, null) as ObservableWebView).apply {
            setOnTouchListener { _, _ ->
                collapseSearchView()
                false
            }
            setOnScrollChangedCallback { _, t, _, oldt ->
                if (oldt < t) {
                    searchButton.hide()
                } else {
                    searchButton.show()
                }
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    presenter.onWebViewUrlClicked(url)
                    return true
                }
            }
            setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    if (presenter.isNightMode) R.color.night_mode_bg else R.color.white
                )
            )
            addView(this, word)
        }
    }

    override fun showLoadingProgress(isVisible: Boolean) {
        progressBar.isVisible = isVisible
    }

    override fun showEmptyView(isVisible: Boolean) {
        hintTextView.isVisible = isVisible
    }

    override fun showSuggestionsView(isVisible: Boolean) {
        suggestionsListView.isVisible = isVisible
    }

    override fun showTranslationView(isVisible: Boolean) {
        viewPagerContainer.isVisible = isVisible
    }

    override fun showKazakhLetters(isVisible: Boolean) {
        kazCharsView.isVisible = isVisible
    }

    override fun collapseSearchView() {
        val searchItem = toolbar.menu.findItem(R.id.action_search)
        if (searchItem.isActionViewExpanded) {
            searchItem.collapseActionView()
        }
    }

    override fun showNoNetworkError() {
        showToast(R.string.unable_to_translate_phrase_check_internet_connection)
    }

    override fun onSuggestionsLoaded(suggestions: List<String>) {
        val adapter = ArrayAdapter(requireContext(), itemSuggestionLayoutId, suggestions)
        suggestionsListView.adapter = adapter
    }

    override fun playSound(url: String) {
        with(mediaPlayer) {
            try {
                setDataSource(url)
                prepareAsync()
            } catch (e: IllegalStateException) {
                reset()
            }
        }
    }

    override fun showSearchMenuItem(isVisible: Boolean) {
        toolbar.menu.findItem(R.id.action_search).isVisible = isVisible
    }

    override fun showWordMenuItems(isVisible: Boolean) {
        with(toolbar.menu) {
            findItem(R.id.action_favorite).isVisible = isVisible
            findItem(R.id.action_copy).isVisible = isVisible
            findItem(R.id.action_share).isVisible = isVisible
            findItem(R.id.action_play_sound).isVisible = isVisible
        }
    }

    override fun updateWordMenuItemsStates(word: Word) {
        toolbar.menu.findItem(R.id.action_favorite).setIcon(
            if (word.isFavorite) {
                R.drawable.ic_star_white_24dp
            } else {
                R.drawable.ic_star_border_white_24dp
            }
        )

        toolbar.menu.findItem(R.id.action_play_sound).setIcon(
            if (word.hasAudio()) {
                R.drawable.ic_volume_up_white_24dp
            } else {
                R.drawable.ic_volume_up_gray_24dp
            }
        )
    }

    override fun openBrowser(url: String) {
        startActivity(
            Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(url))
        )
    }

    override fun showError(message: String) {
        showToast(message)
    }

    override fun showTranslationCourse(language: String) {
        val iconResId = if (Lang.isKazakh(language)) {
            R.drawable.ic_fab_kk_ru
        } else {
            R.drawable.ic_fab_ru_kk
        }
        searchButton.setImageResource(iconResId)
    }

    override fun showSearchButton() {
        searchButton.setImageResource(R.drawable.ic_search_white)
    }

    override fun setSearchPhrase(phrase: String) {
        searchView?.post {
            searchView?.setQuery(phrase, false)
        }
    }

    override fun switchTheme(isNightMode: Boolean) {
        context?.let {
            if (isNightMode) {
                suggestionsListView.setBackgroundColor(ContextCompat.getColor(it, R.color.night_mode_bg))
                suggestionsListView.divider = ColorDrawable(ContextCompat.getColor(it, R.color.night_mode_text))
                suggestionsListView.dividerHeight = 1
                // FIXME: Why lines below repeated?
                rootView.setBackgroundColor(ContextCompat.getColor(it, R.color.night_mode_bg))
                rootView.setBackgroundColor(ContextCompat.getColor(it, R.color.night_mode_bg))
                hintTextView.setTextColor(ContextCompat.getColor(it, R.color.night_mode_text))
                hintTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_search_white)
                viewPagerContainer.setBackgroundColor(ContextCompat.getColor(it, R.color.night_mode_bg))

                itemSuggestionLayoutId = R.layout.list_item_suggestion_night
            } else {
                itemSuggestionLayoutId = android.R.layout.simple_list_item_1
            }
        }
    }

    // TODO: Refactor
    @Suppress("LoopWithTooManyJumpStatements")
    override fun onFavoriteStateChanged(word: Word, isFavorite: Boolean) {
        val words = mainPagerAdapter.words
        for (w in words) {
            if (!w.isSameWord(word)) {
                continue
            }
            w.favourite = if (isFavorite) 1 else 0
            if (currentWord != null && w.isSameWord(currentWord)) {
                val favoriteIconResId = if (isFavorite) {
                    R.drawable.ic_star_white_24dp
                } else {
                    R.drawable.ic_star_border_white_24dp
                }
                toolbar.menu.findItem(R.id.action_favorite)?.setIcon(favoriteIconResId)
            }
            break
        }
    }

    // TODO Refactor strings localization
    override fun showShareScreen(word: Word) {
        val language = if (Lang.isKazakh(word.langTo)) "казахском" else "русском"
        val text =
            """
            "${word.phrase}" на $language будет ${word.urlShort}. С помощью @sozdik для Android
            """.trimIndent()
        val shareIntent = Intent(Intent.ACTION_SEND)
            .setType(TEXT_PLAIN_TYPE)
            .putExtra(Intent.EXTRA_SUBJECT, word.phrase)
            .putExtra(Intent.EXTRA_TEXT, text)
        startActivity(shareIntent)
    }

    override fun copyToClipboard(word: Word) {
        val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Translate", "${word.phrase} - ${word.translateAsText}")
        clipboardManager.setPrimaryClip(clipData)
        showToast(R.string.translate_text_copied_to_buffer)
    }

    override fun setToolbarTitle(title: String) {
        toolbar.title = title
    }

    override fun vibrate(milliseconds: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect =
                VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(milliseconds)
        }
    }

    override fun setKazakhCharsType(type: KazCharsView.Type) {
        kazCharsView.type = type
    }

    private fun setTranslationContent(word: Word?, webView: WebView) {
        if (word != null) {
            val content: String
            content = if (word.isRight) {
                val phraseIpa = if (word.phraseIpa.isNullOrEmpty()) {
                    ""
                } else {
                    getString(R.string.translation_mfa, word.phraseIpa)
                }
                getString(
                    R.string.translation_found,
                    css,
                    word.phraseAcute,
                    phraseIpa + word.getFormattedTranslation()
                )
            } else {
                getString(
                    R.string.translation_not_found_suggestions,
                    css,
                    word.phrase,
                    word.similarPhrasesAsHtml
                )
            }
            // if don't set baseUrl then method "shouldOverrideUrlLoading" won't be called
            webView.loadDataWithBaseURL(
                BASE_WEB_VIEW_URL,
                content,
                WEB_VIEW_MIME_TYPE,
                WEB_VIEW_ENCODING,
                null
            )
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val phrase = suggestionsListView.adapter.getItem(position).toString()
        presenter.translatePhrase(phrase)
    }

    private fun setupViews() {
        suggestionsListView.onItemClickListener = this

        viewPager.adapter = mainPagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) = Unit

            override fun onPageSelected(position: Int) {
                val word = mainPagerAdapter.getWord(position)
                presenter.viewPagerPageChanged(word!!)

                if (!searchButton.isShown) {
                    searchButton.show()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        pageIndicator.setViewPager(viewPager)

        searchButton.setOnClickListener { presenter.onFabClicked() }
        hintTextView.setOnClickListener { expandSearchView() }
        kazCharsView.kazCharsClickListener = { letter -> onKazakhLetterClick(letter) }
    }

    private fun setupMenu() {
        val searchItem = toolbar.menu.findItem(R.id.action_search)
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                presenter.searchViewExpanded()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                presenter.searchViewCollapsed(currentWord)
                return true
            }
        })
        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = (searchItem.actionView as SearchView).apply {
            queryHint = getString(R.string.search_hint)
            maxWidth = Integer.MAX_VALUE
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    presenter.translatePhrase(query)
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    searchSuggestions(newText)
                    return true
                }
            })
            etFindWord = findViewById(androidx.appcompat.R.id.search_src_text)
        }

        if (!phraseFromClipboard.isNullOrBlank()) {
            presenter.translatePhrase(phraseFromClipboard!!)
            phraseFromClipboard = null
        }

        presenter.onCreateOptionsMenu()
    }

    private fun setupToolbar() {
        with(toolbar) {
            setOnClickListener { expandSearchView() }
            inflateMenu(R.menu.menu_dictionary)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_copy -> presenter.onCopyToClipboardClick()
                    R.id.action_share -> presenter.onShareWordClick()
                    R.id.action_play_sound -> presenter.playSound()
                    R.id.action_favorite -> presenter.onFavoriteClick()
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun onKazakhLetterClick(letter: Char) {
        val lowerCaseLetter = letter.toLowerCase()
        presenter.onKazakhLetterClick()

        etFindWord?.let { findWordEditText ->
            val cursorPosition = findWordEditText.selectionStart

            var text = findWordEditText.text.toString()
            if (findWordEditText.hasSelection()) {
                text = text.substring(0, findWordEditText.selectionStart) +
                    text.substring(findWordEditText.selectionEnd)
                findWordEditText.setText(text)
            }
            text = findWordEditText.text.toString()
            val newText = text.substring(0, cursorPosition) + lowerCaseLetter +
                text.substring(cursorPosition, text.length)
            findWordEditText.setText(newText)
            findWordEditText.setSelection(cursorPosition + 1)
        }
    }

    @Suppress("ReturnCount")
    fun onBackPressed(): Boolean {
        val searchItem = toolbar.menu.findItem(R.id.action_search)
        if (searchItem.isActionViewExpanded) {
            searchItem.collapseActionView()
            return true
        }
        // TODO: Encapsulate in PrefsManager class
        val actionBack =
            prefsManager.getString(getString(R.string.pref_key_action_back), "back_action_prev_translation")
        if ("back_action_prev_translation" == actionBack) {
            var currentPageIndex = viewPager.currentItem
            if (currentPageIndex > 0) {
                viewPager.currentItem = --currentPageIndex
                return true
            }
        }
        return false
    }

    override fun onKeyboardStateChanged(state: KeyboardState) {
        presenter.onKeyboardStateChanged(state)
    }
}