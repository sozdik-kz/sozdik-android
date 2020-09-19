package kz.sozdik.translation.presentation

import kz.sozdik.base.BaseMvpView

interface TranslateView : BaseMvpView {

    fun onSuccess(text: String)

    fun showKazakhLetters(isVisible: Boolean)

    fun showTranslationCourse(lang: String)

    fun vibrate(milliseconds: Long)

    fun onKeyboardOpened()

    fun onKeyboardClosed()
}