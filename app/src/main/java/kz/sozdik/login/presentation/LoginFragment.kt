package kz.sozdik.login.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_login.*
import kz.sozdik.R
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.presentation.utils.popScreen
import kz.sozdik.presentation.utils.showToast
import kz.sozdik.register.di.DaggerRegistrationPresenterComponent
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import timber.log.Timber

private const val RC_SIGN_IN = 10

class LoginFragment :
    MvpAppCompatFragment(R.layout.fragment_login),
    LoginView {

    companion object {
        fun create(): LoginFragment = LoginFragment()
    }

    private var googleSignInClient: GoogleSignInClient? = null
    private var callbackManager: CallbackManager? = null

    private var firebaseAuth: FirebaseAuth? = null

    @InjectPresenter
    lateinit var presenter: LoginPresenter

    @ProvidePresenter
    internal fun providePresenter(): LoginPresenter =
        DaggerRegistrationPresenterComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
            .getLoginPresenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initGoogle()
        initFacebook()
        firebaseAuth = FirebaseAuth.getInstance()

        toolbar.setNavigationOnClickListener { popScreen() }

        loginButton.setOnClickListener {
            presenter.login(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }

        googleSignInButton.setOnClickListener {
            startActivityForResult(googleSignInClient?.signInIntent, RC_SIGN_IN)
        }

        facebookSignInButton.setOnClickListener {
            LoginManager
                .getInstance()
                .logInWithReadPermissions(this, listOf("public_profile", "email"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Timber.e(e, "Google sign in failed")
            }
        }

        // Pass the activity result back to the Facebook SDK
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Timber.d("firebaseAuthWithGoogle ${account.id}")

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth
            ?.signInWithCredential(credential)
            ?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    firebaseAuth?.currentUser?.let {
                        presenter.loginWithGoogleUser(it, account.email.orEmpty())
                    }
                } else {
                    Timber.e(task.exception, "signInWithCredential failed")
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Timber.d("handleFacebookAccessToken ${token.userId}")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth
            ?.signInWithCredential(credential)
            ?.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    firebaseAuth?.currentUser?.let {
                        presenter.loginWithFacebookUser(it)
                    }
                } else {
                    Timber.e(task.exception, "signInWithCredential: failure")
                    showToast(getString(R.string.error_default_exception))
                }
            }
    }

    override fun onSuccess() {
        // TODO: Inform previous fragment about successful login
        popScreen()
    }

    override fun onError(message: String) {
        showToast(message)
    }

    override fun showLoading(isVisible: Boolean) {
        progressBar.isVisible = isVisible
        loginButton.isInvisible = isVisible
    }

    override fun setEmailError(message: String?) {
        emailInputLayout.error = message
    }

    override fun setPasswordError(message: String?) {
        passwordInputLayout.error = message
    }

    private fun initGoogle() {
        googleSignInClient = GoogleSignIn.getClient(
            requireContext(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build()
        )
    }

    private fun initFacebook() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                }

                override fun onError(exception: FacebookException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut()
                    }
                    exception.message?.let { showToast(it) }
                }
            }
        )
    }
}