package kz.sozdik.register.di

import dagger.BindsInstance
import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.register.presentation.codeconfirm.CodeConfirmPresenter

@Presenter
@Component(
    dependencies = [AppDependency::class]
)
interface CodeConfirmationPresenterComponent {
    fun getConfirmCodePresenter(): CodeConfirmPresenter

    @Component.Factory
    interface Factory {
        fun create(
            appDependency: AppDependency,
            @BindsInstance confirmationToken: String
        ): CodeConfirmationPresenterComponent
    }
}