package kz.sozdik.login.domain.model

sealed class LoginResult {
    class Token(val token: String) : LoginResult()
    object Error : LoginResult()
}