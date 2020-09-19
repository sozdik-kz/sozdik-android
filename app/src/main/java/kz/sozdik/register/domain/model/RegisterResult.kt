package kz.sozdik.register.domain.model

sealed class RegisterResult {
    class Success(val email: String, val confirmationToken: String) : RegisterResult()
    object InappropriateEmailError : RegisterResult()
    object EmailRegisteredError : RegisterResult()
    object EqualsEmailAndPasswordError : RegisterResult()
    object WrongFirstNameError : RegisterResult()
    object WrongLastNameError : RegisterResult()
    object UnknownError : RegisterResult()
}