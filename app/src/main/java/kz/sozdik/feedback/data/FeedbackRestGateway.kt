package kz.sozdik.feedback.data

import com.google.firebase.iid.FirebaseInstanceId
import kz.sozdik.feedback.data.api.FeedbackApi
import kz.sozdik.feedback.domain.FeedbackRemoteGateway
import javax.inject.Inject

class FeedbackRestGateway @Inject constructor(
    private val feedbackApi: FeedbackApi
) : FeedbackRemoteGateway {

    override suspend fun sendFeedback(email: String, name: String, message: String) =
        feedbackApi.postFeedback(
            FirebaseInstanceId.getInstance().token!!,
            email.trim(),
            name.trim(),
            message.trim()
        )
}