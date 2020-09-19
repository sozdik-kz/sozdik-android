package kz.sozdik.register.domain.model

sealed class ConfirmationCodeResult {
    class Token(val authToken: String) : ConfirmationCodeResult()
    object EmailNotRegisteredError : ConfirmationCodeResult()
    object WrongCodeError : ConfirmationCodeResult()
    object UnknownError : ConfirmationCodeResult()
}