package kz.sozdik.register.presentation.codeconfirm

import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.presentation.utils.ErrorMessageFactory
import kz.sozdik.register.domain.RegistrationInteractor
import kz.sozdik.register.domain.model.ConfirmationCodeResult
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class CodeConfirmPresenter @Inject constructor(
    private val confirmationToken: String,
    private val registrationInteractor: RegistrationInteractor,
    private val resourceManager: ResourceManager
) : BaseMvpPresenter<CodeConfirmView>() {

    fun confirmRegistration(code: String) {
        if (code.isBlank()) {
            viewState.onError(resourceManager.getString(R.string.code_confirm_error_empty_code))
            return
        }

        viewState.showButtonConfirm(false)
        viewState.showViewLoading(true)

        launch {
            try {
                val data = registrationInteractor.confirmCode(code, confirmationToken)
                handleResult(data)

                viewState.showViewLoading(false)
                viewState.showButtonConfirm(true)
            } catch (e: Throwable) {
                viewState.onError(ErrorMessageFactory.create(resourceManager, e))
                viewState.showViewLoading(false)
                viewState.showButtonConfirm(true)
            }
        }
    }

    private fun handleResult(result: ConfirmationCodeResult) {
        when (result) {
            is ConfirmationCodeResult.Token -> viewState.onSuccess()
            is ConfirmationCodeResult.WrongCodeError ->
                viewState.onError(resourceManager.getString(R.string.code_confirm_error_wrong_code))
            is ConfirmationCodeResult.EmailNotRegisteredError ->
                viewState.onError(
                    resourceManager.getString(R.string.code_confirm_error_email_not_registered)
                )
            else -> viewState.onError(resourceManager.getString(R.string.error_default_exception))
        }
    }
}