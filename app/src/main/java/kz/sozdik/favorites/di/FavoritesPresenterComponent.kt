package kz.sozdik.favorites.di

import dagger.BindsInstance
import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.favorites.presentation.FavoritesPresenter

@Presenter
@Component(
    dependencies = [AppDependency::class]
)
interface FavoritesPresenterComponent {
    fun getFavoritesPresenter(): FavoritesPresenter

    @Component.Factory
    interface Factory {
        fun create(
            appDeps: AppDependency,
            @BindsInstance langFrom: String
        ): FavoritesPresenterComponent
    }
}