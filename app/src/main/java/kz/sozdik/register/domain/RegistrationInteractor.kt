package kz.sozdik.register.domain

import kz.sozdik.core.AuthUtils
import kz.sozdik.profile.domain.ProfileInteractor
import kz.sozdik.register.domain.model.ConfirmationCodeResult
import kz.sozdik.register.domain.model.RegisterResult
import javax.inject.Inject

class RegistrationInteractor @Inject constructor(
    private val registerRemoteGateway: RegisterRemoteGateway,
    private val profileInteractor: ProfileInteractor
) {

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        deviceToken: String
    ): RegisterResult =
        registerRemoteGateway.register(email, password, firstName, lastName, deviceToken)

    suspend fun confirmCode(code: String, confirmationToken: String): ConfirmationCodeResult {
        val result = registerRemoteGateway.confirmCode(code, confirmationToken)
        if (result is ConfirmationCodeResult.Token) {
            AuthUtils.setAuthToken(result.authToken)
            profileInteractor.loadProfile()
        }
        return result
    }
}