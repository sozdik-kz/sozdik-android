package kz.sozdik.translation.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_translation.*
import kz.sozdik.R
import kz.sozdik.core.utils.getViewModel
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.di.viewmodel.ViewModelFactory
import kz.sozdik.main.KeyboardState
import kz.sozdik.main.KeyboardStateListener
import kz.sozdik.presentation.utils.hideKeyboard
import kz.sozdik.presentation.utils.openInChromeTab
import kz.sozdik.presentation.utils.showToast
import kz.sozdik.translation.di.DaggerTranslationComponent
import kz.sozdik.translation.di.TranslationComponent
import javax.inject.Inject

class TranslationFragment :
    Fragment(R.layout.fragment_translation),
    KeyboardStateListener {

    private val component: TranslationComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerTranslationComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
    }

    @Inject
    lateinit var factory: ViewModelFactory<TranslationViewModel>

    @Inject
    lateinit var clipboardManager: ClipboardManager

    private lateinit var viewModel: TranslationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)

        viewModel = getViewModel(TranslationViewModel::class.java, factory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.translationResultLiveData.observe(viewLifecycleOwner) { translation ->
            hideKeyboard()
            translateTextView.text = translation
        }

        viewModel.showTranslateViewLiveData.observe(viewLifecycleOwner) {
            translateView.isVisible = it
        }

        viewModel.translationCourseTitleLiveData.observe(viewLifecycleOwner) {
            toolbar.menu.findItem(R.id.action_translate_course).title = it
        }

        viewModel.errorMessageLiveData.observe(viewLifecycleOwner) { showToast(it) }

        viewModel.showKzLettersLiveData.observe(viewLifecycleOwner) {
            kazCharsView.isVisible = it
        }

        toolbar.inflateMenu(R.menu.menu_translate)
        toolbar.menu.findItem(R.id.action_translate_course).setOnMenuItemClickListener {
            viewModel.toggleTranslationCourse()
            return@setOnMenuItemClickListener true
        }

        translateTextView.doAfterTextChanged {
            copyButton.isVisible = !it.isNullOrEmpty()
        }
        textEditText.doAfterTextChanged {
            clearButton.isVisible = !it.isNullOrEmpty()
        }

        searchButton.setOnClickListener {
            viewModel.translate(textEditText.text.toString())
        }
        clearButton.setOnClickListener {
            textEditText?.text?.clear()
            viewModel.onClearPressed()
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
        val lowerCaseLetter = letter.lowercaseChar()

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
        viewModel.onKeyboardStateChanged(state)
    }

    private fun getTextFromClipboard(): String {
        val item = clipboardManager.primaryClip?.getItemAt(0)
        return item?.text.toString()
    }

    private fun copyToClipboard(text: String) {
        val clipData = ClipData.newPlainText(requireContext().packageName, text)
        clipboardManager.setPrimaryClip(clipData)
        showToast(R.string.translate_text_copied_to_buffer)
    }
}