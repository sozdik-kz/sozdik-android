package kz.sozdik.screen

import kz.sozdik.R
import kz.sozdik.feedback.presentation.FeedbackFragment

object FeedbackScreen : KScreen<FeedbackScreen>() {

    override val layoutId: Int? = R.layout.fragment_feedback
    override val viewClass: Class<*>? = FeedbackFragment::class.java

//    val sendButton = KButton { withId(R.id.sendButton) }
//    val progressBar = KProgressBar { withId(R.id.progressBar) }
//    val emailEditText = KEditText { withId(R.id.emailEditText) }
//    val emailInputLayout = KTextInputLayout { withId(R.id.emailInputLayout) }
//    val nameEditText = KEditText { withId(R.id.nameEditText) }
//    val nameInputLayout = KTextInputLayout { withId(R.id.nameInputLayout) }
//    val messageEditText = KEditText { withId(R.id.messageEditText) }
//    val messageInputLayout = KTextInputLayout { withId(R.id.messageInputLayout) }
}