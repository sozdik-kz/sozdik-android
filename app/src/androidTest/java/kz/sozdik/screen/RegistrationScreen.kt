package kz.sozdik.screen

import io.github.kakaocup.kakao.check.KCheckBox
import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.edit.KTextInputLayout
import io.github.kakaocup.kakao.progress.KProgressBar
import io.github.kakaocup.kakao.text.KButton
import kz.sozdik.R
import kz.sozdik.register.presentation.registration.RegistrationFragment

object RegistrationScreen : KScreen<RegistrationScreen>() {

    override val layoutId: Int? = R.layout.fragment_registration
    override val viewClass: Class<*>? = RegistrationFragment::class.java

    val registerButton = KButton { withId(R.id.continueButton) }
    val loginButton = KButton { withId(R.id.loginButton) }
    val progressBar = KProgressBar { withId(R.id.progressBar) }
    val firstNameEditText = KEditText { withId(R.id.firstNameEditText) }
    val firstNameInputLayout = KTextInputLayout { withId(R.id.firstNameInputLayout) }
    val lastNameEditText = KEditText { withId(R.id.lastNameEditText) }
    val lastNameInputLayout = KTextInputLayout { withId(R.id.lastNameInputLayout) }
    val emailEditText = KEditText { withId(R.id.emailEditText) }
    val emailInputLayout = KTextInputLayout { withId(R.id.emailInputLayout) }
    val passwordEditText = KEditText { withId(R.id.passwordEditText) }
    val passwordInputLayout = KTextInputLayout { withId(R.id.passwordInputLayout) }
    val termsOfUseCheckBox = KCheckBox { withId(R.id.termsOfUseCheckBox) }
//    val termsOfUseTextView = KTextView { withId(R.id.termsOfUseTextView) }
    val privacyPolicyCheckBox = KCheckBox { withId(R.id.privacyPolicyCheckBox) }
//    val privacyPolicyTextView = KTextView { withId(R.id.privacyPolicyTextView) }
}