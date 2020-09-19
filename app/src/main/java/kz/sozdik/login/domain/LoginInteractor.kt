package kz.sozdik.login.domain

import kz.sozdik.core.AuthUtils
import kz.sozdik.history.domain.HistoryInteractor
import kz.sozdik.login.data.api.model.SocialAuthOutput
import kz.sozdik.login.domain.gateway.LoginRemoteGateway
import kz.sozdik.login.domain.model.LoginResult
import kz.sozdik.profile.domain.ProfileInteractor
import javax.inject.Inject

class LoginInteractor @Inject constructor(
    private val loginRemoteGateway: LoginRemoteGateway,
    private val profileInteractor: ProfileInteractor,
    private val historyInteractor: HistoryInteractor
) {

    suspend fun socialAuth(info: SocialAuthOutput): LoginResult =
        loginRemoteGateway.socialAuth(info).also {
            handleLoginResult(it)
        }

    suspend fun login(
        email: String,
        password: String,
        deviceToken: String
    ): LoginResult =
        loginRemoteGateway.login(email, password, deviceToken).also {
            handleLoginResult(it)
        }

    private suspend fun handleLoginResult(loginResult: LoginResult) {
        if (loginResult is LoginResult.Token) {
            historyInteractor.clearAllWordsLocally()
            AuthUtils.setAuthToken(loginResult.token)
            profileInteractor.loadProfile()
        }
    }
}