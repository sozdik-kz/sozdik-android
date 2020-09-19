package kz.sozdik.screen

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.edit.KTextInputLayout
import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.text.KButton
import kz.sozdik.R
import kz.sozdik.login.presentation.LoginFragment

object LoginScreen : KScreen<LoginScreen>() {

    override val layoutId: Int? = R.layout.fragment_login
    override val viewClass: Class<*>? = LoginFragment::class.java

    val loginButton = KButton { withId(R.id.loginButton) }
    val googleSignInButton = KButton { withId(R.id.googleSignInButton) }
    val facebookSignInButton = KButton { withId(R.id.facebookSignInButton) }
    val progressBar = KProgressBar { withId(R.id.progressBar) }
    val emailEditText = KEditText { withId(R.id.emailEditText) }
    val emailInputLayout = KTextInputLayout { withId(R.id.emailInputLayout) }
    val passwordEditText = KEditText { withId(R.id.passwordEditText) }
    val passwordInputLayout = KTextInputLayout { withId(R.id.passwordInputLayout) }
}