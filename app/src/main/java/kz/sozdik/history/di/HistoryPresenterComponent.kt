package kz.sozdik.history.di

import dagger.BindsInstance
import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.history.presentation.HistoryPresenter

@Presenter
@Component(
    dependencies = [AppDependency::class]
)
interface HistoryPresenterComponent {
    fun getHistoryPresenter(): HistoryPresenter

    @Component.Factory
    interface Factory {
        fun create(
            appDeps: AppDependency,
            @BindsInstance langFrom: String
        ): HistoryPresenterComponent
    }
}