package kz.sozdik.feedback.domain

interface FeedbackRemoteGateway {
    suspend fun sendFeedback(email: String, name: String, message: String)
}