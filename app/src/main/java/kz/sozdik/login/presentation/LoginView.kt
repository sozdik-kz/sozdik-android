package kz.sozdik.login.presentation

import moxy.MvpView

interface LoginView : MvpView {

    fun onSuccess()

    fun onError(message: String)

    fun showLoading(isVisible: Boolean)

    fun setEmailError(message: String?)

    fun setPasswordError(message: String?)
}