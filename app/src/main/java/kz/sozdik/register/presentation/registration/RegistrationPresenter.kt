package kz.sozdik.register.presentation.registration

import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.presentation.utils.EmailValidator
import kz.sozdik.presentation.utils.ErrorMessageFactory
import kz.sozdik.register.domain.RegistrationInteractor
import kz.sozdik.register.domain.model.RegisterResult
import moxy.InjectViewState
import javax.inject.Inject

private const val MIN_PASSWORD_LENGTH = 6

@InjectViewState
class RegistrationPresenter @Inject constructor(
    private val registrationInteractor: RegistrationInteractor,
    private val resourceManager: ResourceManager
) : BaseMvpPresenter<RegistrationView>() {

    @Suppress("ReturnCount")
    fun register(email: String, password: String, firstName: String, lastName: String) {
        if (firstName.isBlank()) {
            viewState.showFirstNameError(resourceManager.getString(R.string.register_error_wrong_first_name))
            return
        }
        viewState.showFirstNameError(null)

        if (lastName.isBlank()) {
            viewState.showLastNameError(resourceManager.getString(R.string.register_error_wrong_last_name))
            return
        }
        viewState.showLastNameError(null)

        if (!EmailValidator.isValidEmail(email)) {
            viewState.showEmailError(resourceManager.getString(R.string.register_error_wrong_email))
            return
        }
        viewState.showEmailError(null)

        if (password.length < MIN_PASSWORD_LENGTH) {
            viewState.showPasswordError(resourceManager.getString(R.string.register_error_wrong_password))
            return
        }
        viewState.showPasswordError(null)

        viewState.showViewLoading(true)
        viewState.showButtonContinue(false)

        launch {
            try {
                // TODO: Get rid of deprecated 'token' invocation
                val deviceToken = FirebaseInstanceId.getInstance().token.orEmpty()

                val data = registrationInteractor.register(
                    email,
                    password,
                    firstName,
                    lastName,
                    deviceToken
                )
                handleResult(data)

                viewState.showViewLoading(false)
                viewState.showButtonContinue(true)
            } catch (e: Throwable) {
                viewState.showViewLoading(false)
                viewState.showButtonContinue(true)
                viewState.onError(ErrorMessageFactory.create(resourceManager, e))
            }
        }
    }

    private fun handleResult(result: RegisterResult) {
        if (result is RegisterResult.Success) {
            viewState.showCodeConfirmationScreen(result.email, result.confirmationToken)
        } else {
            val errorMessageResId = when (result) {
                is RegisterResult.InappropriateEmailError -> R.string.register_error_email_unavailable
                is RegisterResult.EmailRegisteredError -> R.string.register_error_email_already_registered
                is RegisterResult.EqualsEmailAndPasswordError -> R.string.register_error_password_and_login_coincide
                is RegisterResult.WrongFirstNameError -> R.string.register_error_wrong_first_name
                is RegisterResult.WrongLastNameError -> R.string.register_error_wrong_last_name
                else -> R.string.something_went_wrong
            }
            viewState.onError(resourceManager.getString(errorMessageResId))
        }
    }
}