package kz.sozdik.feedback.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_feedback.*
import kz.sozdik.R
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.feedback.di.DaggerFeedbackPresenterComponent
import kz.sozdik.presentation.utils.showToast
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class FeedbackFragment :
    MvpAppCompatFragment(R.layout.fragment_feedback),
    FeedbackView {

    companion object {
        fun create() = FeedbackFragment()
    }

    @InjectPresenter
    lateinit var feedbackPresenter: FeedbackPresenter

    @ProvidePresenter
    internal fun providePresenter(): FeedbackPresenter =
        DaggerFeedbackPresenterComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
            .getFeedbackPresenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendButton.setOnClickListener {
            sendFeedback()
        }
        toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun sendFeedback() {
        feedbackPresenter.sendFeedback(
            emailEditText.text.toString(),
            nameEditText.text.toString(),
            messageEditText.text.toString()
        )
    }

    override fun onError(message: String) {
        showToast(message)
    }

    override fun showProgressBar(isVisible: Boolean) {
        progressBar.isVisible = isVisible
        sendButton.isInvisible = isVisible
    }

    override fun onFeedbackCreated() {
        showToast(R.string.feedback_message_sent)
        activity?.onBackPressed()
    }

    override fun showNameError(message: String?) {
        nameInputLayout.error = message
    }

    override fun showEmailError(message: String?) {
        emailInputLayout.error = message
    }

    override fun showMessageError(message: String?) {
        messageInputLayout.error = message
    }

    override fun enableEmailEditText(isEnabled: Boolean) {
        emailEditText.isEnabled = isEnabled
    }

    override fun enableNameEditText(isEnabled: Boolean) {
        nameEditText.isEnabled = isEnabled
    }

    override fun setName(name: String) {
        nameEditText.setText(name)
    }

    override fun setEmail(email: String) {
        emailEditText.setText(email)
    }
}