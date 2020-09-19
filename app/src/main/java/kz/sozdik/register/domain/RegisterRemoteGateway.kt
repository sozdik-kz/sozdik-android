package kz.sozdik.register.domain

import kz.sozdik.register.domain.model.ConfirmationCodeResult
import kz.sozdik.register.domain.model.RegisterResult

interface RegisterRemoteGateway {
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        deviceToken: String
    ): RegisterResult

    suspend fun confirmCode(code: String, confirmationToken: String): ConfirmationCodeResult
}