package kz.sozdik.register.presentation.registration

import moxy.MvpView
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.OneExecutionStateStrategy

interface RegistrationView : MvpView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showCodeConfirmationScreen(email: String, confirmationToken: String)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun onError(message: String)

    fun showViewLoading(isVisible: Boolean)

    fun showButtonContinue(isVisible: Boolean)

    fun showFirstNameError(message: String?)

    fun showLastNameError(message: String?)

    fun showEmailError(message: String?)

    fun showPasswordError(message: String?)
}