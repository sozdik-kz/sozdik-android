package kz.sozdik.feedback.presentation

import moxy.MvpView

interface FeedbackView : MvpView {

    fun onError(message: String)

    fun showProgressBar(isVisible: Boolean)

    fun onFeedbackCreated()

    fun showNameError(message: String?)

    fun showEmailError(message: String?)

    fun showMessageError(message: String?)

    fun enableEmailEditText(isEnabled: Boolean)

    fun enableNameEditText(isEnabled: Boolean)

    fun setName(name: String)

    fun setEmail(email: String)
}