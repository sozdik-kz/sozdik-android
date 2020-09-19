package kz.sozdik.register.data.gateway

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.register.data.api.RegistrationApi
import kz.sozdik.register.domain.RegisterRemoteGateway
import kz.sozdik.register.domain.model.ConfirmationCodeResult
import kz.sozdik.register.domain.model.ConfirmationCodeResult.EmailNotRegisteredError
import kz.sozdik.register.domain.model.ConfirmationCodeResult.Token
import kz.sozdik.register.domain.model.ConfirmationCodeResult.UnknownError
import kz.sozdik.register.domain.model.ConfirmationCodeResult.WrongCodeError
import kz.sozdik.register.domain.model.RegisterResult
import kz.sozdik.register.domain.model.RegisterResult.EmailRegisteredError
import kz.sozdik.register.domain.model.RegisterResult.EqualsEmailAndPasswordError
import kz.sozdik.register.domain.model.RegisterResult.InappropriateEmailError
import kz.sozdik.register.domain.model.RegisterResult.Success
import kz.sozdik.register.domain.model.RegisterResult.WrongFirstNameError
import kz.sozdik.register.domain.model.RegisterResult.WrongLastNameError
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val EMAIL_NOT_REGISTERED = 0
private const val WRONG_CONFIRMATION_CODE = -521

private const val EMAIL_IS_NOT_FROM_WHITE_LIST = -501
private const val EMAIL_ALREADY_REGISTERED = -504
private const val EQUALS_EMAIL_AND_PASSWORD = -513
private const val WRONG_FIRST_NAME = -514
private const val WRONG_LAST_NAME = -515

class RegistrationRestGateway @Inject constructor(
    private val registrationApi: RegistrationApi
) : RegisterRemoteGateway {

    override suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        deviceToken: String
    ): RegisterResult {
        val response = registrationApi.register(
            deviceToken,
            email,
            password,
            firstName,
            lastName,
            TimeUnit.HOURS.convert(TimeZone.getDefault().rawOffset.toLong(), TimeUnit.MILLISECONDS)
        )
        return when (response.result) {
            ResponseWrapper.RESULT_OK -> Success(response.data.email, response.data.confirmToken)
            EMAIL_IS_NOT_FROM_WHITE_LIST -> InappropriateEmailError
            EMAIL_ALREADY_REGISTERED -> EmailRegisteredError
            EQUALS_EMAIL_AND_PASSWORD -> EqualsEmailAndPasswordError
            WRONG_FIRST_NAME -> WrongFirstNameError
            WRONG_LAST_NAME -> WrongLastNameError
            else -> RegisterResult.UnknownError
        }
    }

    override suspend fun confirmCode(code: String, confirmationToken: String): ConfirmationCodeResult {
        val response = registrationApi.confirmCode(code, confirmationToken)
        return when (response.result) {
            ResponseWrapper.RESULT_OK -> Token(response.data.authToken!!)
            EMAIL_NOT_REGISTERED -> EmailNotRegisteredError
            WRONG_CONFIRMATION_CODE -> WrongCodeError
            else -> UnknownError
        }
    }
}