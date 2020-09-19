package kz.sozdik.register.di

import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.login.presentation.LoginPresenter
import kz.sozdik.register.presentation.registration.RegistrationPresenter

@Presenter
@Component(
    dependencies = [AppDependency::class]
)
interface RegistrationPresenterComponent {
    fun getRegistrationPresenter(): RegistrationPresenter
    fun getLoginPresenter(): LoginPresenter
}