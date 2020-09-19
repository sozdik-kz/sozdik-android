package kz.sozdik.translation.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Html
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import kotlinx.android.synthetic.main.fragment_translate.*
import kz.sozdik.R
import kz.sozdik.core.utils.Lang
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.main.KeyboardState
import kz.sozdik.main.KeyboardStateListener
import kz.sozdik.presentation.utils.hideKeyboard
import kz.sozdik.presentation.utils.openInChromeTab
import kz.sozdik.presentation.utils.showToast
import kz.sozdik.translation.di.DaggerTranslatePresenterComponent
import kz.sozdik.translation.di.TranslatePresenterComponent
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject

class TranslateFragment :
    MvpAppCompatFragment(R.layout.fragment_translate),
    TranslateView,
    KeyboardStateListener {

    private val component: TranslatePresenterComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerTranslatePresenterComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
    }

    @InjectPresenter
    lateinit var presenter: TranslatePresenter

    @ProvidePresenter
    internal fun providePresenter(): TranslatePresenter = component.getTranslatePresenter()

    @Inject
    lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.inflateMenu(R.menu.menu_translate)
        toolbar.menu.findItem(R.id.action_translate_course).setOnMenuItemClickListener {
            presenter.toggleTranslationCourse()
            return@setOnMenuItemClickListener true
        }

        translateTextView.doAfterTextChanged {
            copyButton.isVisible = !it.isNullOrEmpty()
            yandexInfoTextView.isVisible = !it.isNullOrEmpty()
        }
        textEditText.doAfterTextChanged {
            clearButton.isVisible = !it.isNullOrEmpty()
        }

        searchButton.setOnClickListener {
            presenter.translate(textEditText.text.toString())
        }
        clearButton.setOnClickListener {
            textEditText?.text?.clear()
            translateTextView.text = ""
        }
        pasteButton.setOnClickListener {
            textEditText.setText(getTextFromClipboard())
        }
        copyButton.setOnClickListener {
            copyToClipboard(translateTextView.text.toString())
        }
        yandexInfoTextView.setOnClickListener {
            context?.openInChromeTab("https://translate.yandex.ru/")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            yandexInfoTextView.text = Html.fromHtml(
                getText(R.string.translate_yandex_link).toString(),
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            yandexInfoTextView.text =
                Html.fromHtml(getText(R.string.translate_yandex_link).toString())
        }

        kazCharsView.kazCharsClickListener = { letter ->
            onKazakhLetterClick(letter)
        }
    }

    private fun onKazakhLetterClick(letter: Char) {
        val lowerCaseLetter = letter.toLowerCase()
        presenter.onKazakhLetterClick()

        val cursorPosition = textEditText.selectionStart

        var text = textEditText.text.toString()
        if (textEditText.hasSelection()) {
            text = text.substring(0, textEditText.selectionStart) +
                text.substring(textEditText.selectionEnd)
            textEditText.setText(text)
        }
        text = textEditText.text.toString()
        val newText = text.substring(0, cursorPosition) + lowerCaseLetter +
            text.substring(cursorPosition, text.length)
        textEditText.setText(newText)
        textEditText.setSelection(cursorPosition + 1)
    }

    override fun onKeyboardStateChanged(state: KeyboardState) {
        presenter.onKeyboardStateChanged(state)
    }

    private fun getTextFromClipboard(): String? {
        val clipBoard =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clipData = clipBoard?.primaryClip
        val item = clipData?.getItemAt(0)
        return item?.text.toString()
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(context?.packageName, text)
        clipboardManager.setPrimaryClip(clipData)
        showToast(R.string.translate_text_copied_to_buffer)
    }

    override fun onSuccess(text: String) {
        hideKeyboard()
        translateTextView.text = text
    }

    override fun showMessage(message: String) {
        showToast(message)
    }

    override fun showKazakhLetters(isVisible: Boolean) {
        kazCharsView.isVisible = isVisible
    }

    override fun showTranslationCourse(lang: String) {
        toolbar.menu.findItem(R.id.action_translate_course).setTitle(
            when (lang) {
                Lang.KAZAKH_CYRILLIC -> R.string.translate_course_kk_ru
                Lang.RUSSIAN -> R.string.translate_course_ru_kk
                else -> R.string.translate_course_ru_kk
            }
        )
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

    override fun onKeyboardOpened() {
        translateView.isVisible = false
    }

    override fun onKeyboardClosed() {
        translateView.isVisible = true
    }
}