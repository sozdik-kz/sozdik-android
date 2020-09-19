package kz.sozdik.base

import moxy.MvpView

interface BaseMvpView : MvpView {

    fun showMessage(message: String)
}