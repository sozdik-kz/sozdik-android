package kz.sozdik.login.presentation

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.login.data.api.model.SocialAuthOutput
import kz.sozdik.login.domain.LoginInteractor
import kz.sozdik.login.domain.model.LoginResult
import kz.sozdik.presentation.utils.EmailValidator
import kz.sozdik.presentation.utils.ErrorMessageFactory
import moxy.InjectViewState
import javax.inject.Inject

private const val FIELDS_PARAM_KEY = "fields"
private const val PICTURE_TYPE_KEY = "picture.type(normal)"

private const val ID_KEY = "id"
private const val FIRST_NAME_KEY = "first_name"
private const val LAST_NAME_KEY = "last_name"
private const val EMAIL_KEY = "email"
private const val PICTURE_KEY = "picture"

@InjectViewState
class LoginPresenter @Inject constructor(
    private val loginInteractor: LoginInteractor,
    private val resourceManager: ResourceManager
) : BaseMvpPresenter<LoginView>() {

    fun loginWithGoogleUser(user: FirebaseUser, googleEmail: String) {
        if (user.providerData.size < 2) {
            return
        }

        // We are taking UserInfo object with index = 1, because it stores real social network id
        // In other hand, UserInfo with index = 0 stores id from Firebase
        val userInfo = user.providerData[1]

        val fullName = user.displayName!!.split(" ")
        var firstName = user.displayName
        var lastName = user.displayName
        if (fullName.size >= 2) {
            firstName = fullName[0]
            lastName = fullName[1]
        }
        val builder = SocialAuthOutput.Builder()
            .email(user.email ?: googleEmail)
            .firstName(firstName)
            .lastName(lastName)
            .socialNetwork(SocialAuthOutput.GOOGLE)
            .socialNetworkId(userInfo.uid)
        if (user.photoUrl != null) {
            builder.avatarUrl(user.photoUrl?.toString())
        }
        socialLogin(builder)
    }

    @Suppress("UnusedPrivateMember")
    fun loginWithFacebookUser(user: FirebaseUser) {
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { jsonObject, _ ->
            var avatarUrl = ""
            if (jsonObject.has(PICTURE_KEY)) {
                avatarUrl = jsonObject
                    .getJSONObject(PICTURE_KEY)
                    .getJSONObject("data")
                    .getString("url")
            }
            val builder = SocialAuthOutput.Builder()
                .socialNetworkId(jsonObject.getString(ID_KEY))
                .firstName(jsonObject.getString(FIRST_NAME_KEY))
                .lastName(jsonObject.getString(LAST_NAME_KEY))
                .email(jsonObject.optString(EMAIL_KEY))
                .avatarUrl(avatarUrl)
                .socialNetwork(SocialAuthOutput.FACEBOOK)
            socialLogin(builder)
        }
        val scopes = listOf(ID_KEY, FIRST_NAME_KEY, LAST_NAME_KEY, EMAIL_KEY, PICTURE_TYPE_KEY)
        val parameters = Bundle()
        parameters.putString(FIELDS_PARAM_KEY, scopes.joinToString())
        request.parameters = parameters
        request.executeAsync()
    }

    private fun socialLogin(builder: SocialAuthOutput.Builder) {
        viewState.showLoading(true)
        builder.deviceToken(FirebaseInstanceId.getInstance().token)
        launch {
            try {
                val result = loginInteractor.socialAuth(builder.build())
                onLoginSuccess(result)
            } catch (e: Throwable) {
                onError(e)
            }
        }
    }

    fun login(email: String, password: String) {
        if (!EmailValidator.isValidEmail(email)) {
            viewState.setEmailError(resourceManager.getString(R.string.login_error_wrong_email))
            viewState.setPasswordError(null)
            return
        }

        if (password.isEmpty()) {
            viewState.setPasswordError(resourceManager.getString(R.string.login_error_empty_password))
            viewState.setEmailError(null)
            return
        }

        viewState.setEmailError(null)
        viewState.setPasswordError(null)
        viewState.showLoading(true)

        // TODO: Get rid of deprecated 'token' invocation
        val deviceToken = FirebaseInstanceId.getInstance().token.orEmpty()

        launch {
            try {
                val result = loginInteractor.login(email, password, deviceToken)
                onLoginSuccess(result)
            } catch (e: Throwable) {
                onError(e)
            }
        }
    }

    private fun onLoginSuccess(loginResult: LoginResult) {
        viewState.showLoading(false)
        if (loginResult is LoginResult.Token) {
            viewState.onSuccess()
        } else {
            viewState.onError(resourceManager.getString(R.string.login_error_wrong_email_or_password))
        }
    }

    private fun onError(t: Throwable) {
        viewState.showLoading(false)
        viewState.onError(ErrorMessageFactory.create(resourceManager, t))
    }
}