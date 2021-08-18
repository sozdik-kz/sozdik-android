package kz.sozdik.feedback.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.feedback.domain.FeedbackInteractor
import kz.sozdik.presentation.utils.EmailValidator
import kz.sozdik.presentation.utils.ErrorMessageFactory
import kz.sozdik.profile.domain.ProfileInteractor
import timber.log.Timber

class FeedbackViewModel @Inject constructor(
    private val feedbackInteractor: FeedbackInteractor,
    private val profileInteractor: ProfileInteractor,
    private val resourceManager: ResourceManager
) : ViewModel() {

    private var isEmailRequired = true

    private val mutableFeedbackState = MutableStateFlow<FeedbackViewState>(FeedbackViewState.Loading)
    val feedbackState: StateFlow<FeedbackViewState>
        get() = mutableFeedbackState

    fun onSendClicked(name: String, email: String, message: String) {
        when {
            name.isEmpty() -> {
                mutableFeedbackState.value = FeedbackViewState.NameError
            }
            isEmailRequired && !EmailValidator.isValidEmail(email) -> {
                mutableFeedbackState.value = FeedbackViewState.EmailError
            }
            message.isEmpty() -> {
                mutableFeedbackState.value = FeedbackViewState.ContentError
            }
            else -> {
                sendFeedBack(email = email, name = name, message = message)
            }
        }
    }

    private fun sendFeedBack(email: String, name: String, message: String) {
        viewModelScope.launch {
            try {
                feedbackInteractor.sendFeedback(email, name, message)
                mutableFeedbackState.value = FeedbackViewState.Sent
            } catch (e: Throwable) {
                Timber.e(e, "Unable to send feedback")
                mutableFeedbackState.value = FeedbackViewState.Error(ErrorMessageFactory.create(resourceManager, e))
            }
        }
    }
}