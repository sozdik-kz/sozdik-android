package kz.sozdik.presentation.utils

object EmailValidator {

    fun isValidEmail(email: CharSequence): Boolean =
        email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}