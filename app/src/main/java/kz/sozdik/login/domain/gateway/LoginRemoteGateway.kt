package kz.sozdik.login.domain.gateway

import kz.sozdik.login.data.api.model.SocialAuthOutput
import kz.sozdik.login.domain.model.LoginResult

interface LoginRemoteGateway {
    suspend fun socialAuth(info: SocialAuthOutput): LoginResult
    suspend fun login(email: String, password: String, deviceToken: String): LoginResult
}