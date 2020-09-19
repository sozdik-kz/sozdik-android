package kz.sozdik.register.presentation.codeconfirm

import moxy.MvpView

interface CodeConfirmView : MvpView {
    fun onSuccess()
    fun onError(message: String)
    fun showViewLoading(isVisible: Boolean)
    fun showButtonConfirm(isVisible: Boolean)
}