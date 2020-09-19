package kz.sozdik.login.data.gateway

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.core.models.ResponseWrapper.RESULT_OK
import kz.sozdik.login.data.api.LoginApi
import kz.sozdik.login.data.api.model.LoginInfo
import kz.sozdik.login.data.api.model.SocialAuthOutput
import kz.sozdik.login.domain.gateway.LoginRemoteGateway
import kz.sozdik.login.domain.model.LoginResult
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LoginRestGateway @Inject constructor(
    private val loginApi: LoginApi,
) : LoginRemoteGateway {

    override suspend fun socialAuth(info: SocialAuthOutput): LoginResult {
        val response = loginApi.socialRegister(
            info.deviceToken,
            info.email,
            info.avatarUrl,
            info.gender,
            info.firstName,
            info.lastName,
            info.birthDate,
            TimeUnit.HOURS.convert(TimeZone.getDefault().rawOffset.toLong(), TimeUnit.MILLISECONDS),
            info.socialNetwork,
            info.socialNetworkId
        )
        return mapToLoginResult(response)
    }

    override suspend fun login(email: String, password: String, deviceToken: String): LoginResult {
        val response = loginApi.login(
            deviceToken,
            email,
            password
        )
        return mapToLoginResult(response)
    }

    private fun mapToLoginResult(response: ResponseWrapper<LoginInfo>): LoginResult =
        when (response.result) {
            RESULT_OK -> LoginResult.Token(response.data.authToken!!)
            else -> LoginResult.Error
        }
}