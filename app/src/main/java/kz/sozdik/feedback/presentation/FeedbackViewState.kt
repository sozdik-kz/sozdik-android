package kz.sozdik.feedback.presentation

sealed class FeedbackViewState {
    object Sent : FeedbackViewState()
    data class Error(val message: String) : FeedbackViewState()
    object Loading : FeedbackViewState()
    object NameError : FeedbackViewState()
    object EmailError : FeedbackViewState()
    object ContentError : FeedbackViewState()
}
