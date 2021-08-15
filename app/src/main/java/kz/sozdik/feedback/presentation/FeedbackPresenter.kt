package kz.sozdik.feedback.presentation

import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.feedback.domain.FeedbackInteractor
import kz.sozdik.presentation.utils.EmailValidator
import kz.sozdik.presentation.utils.ErrorMessageFactory
import kz.sozdik.profile.domain.ProfileInteractor
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class FeedbackPresenter @Inject constructor(
    private val feedbackInteractor: FeedbackInteractor,
    private val profileInteractor: ProfileInteractor,
    private val resourceManager: ResourceManager
) : BaseMvpPresenter<FeedbackView>() {

    private var isEmailRequired = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
/*
        launch {
            val profile = profileInteractor.awaitProfile()
            isEmailRequired = profile.email.isNullOrEmpty()
            viewState.enableEmailEditText(isEmailRequired)
            viewState.enableNameEditText(profile.fullName.isEmpty())
            viewState.setEmail(profile.email)
            viewState.setName(profile.fullName)
        }
*/
    }

    @Suppress("ReturnCount")
    fun sendFeedback(email: String, name: String, message: String) {
        if (name.isBlank()) {
            viewState.showNameError(resourceManager.getString(R.string.feedback_error_empty_name))
            return
        }
        viewState.showNameError(null)

        if (isEmailRequired && !EmailValidator.isValidEmail(email)) {
            viewState.showEmailError(resourceManager.getString(R.string.feedback_error_wrong_email))
            return
        }
        viewState.showEmailError(null)

        if (message.isBlank()) {
            viewState.showMessageError(resourceManager.getString(R.string.feedback_error_empty_text))
            return
        }
        viewState.showMessageError(null)

        launch {
            try {
                feedbackInteractor.sendFeedback(email, name, message)
                viewState.onFeedbackCreated()
            } catch (e: Throwable) {
                Timber.e(e, "Unable to send feedback")
                viewState.onError(ErrorMessageFactory.create(resourceManager, e))
            }
        }
    }
}