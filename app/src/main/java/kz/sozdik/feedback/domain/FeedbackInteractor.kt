package kz.sozdik.feedback.domain

import javax.inject.Inject

class FeedbackInteractor @Inject constructor(
    private val feedbackRemoteGateway: FeedbackRemoteGateway
) {
    suspend fun sendFeedback(email: String, name: String, message: String) =
        feedbackRemoteGateway.sendFeedback(email, name, message)
}