package kz.sozdik.advertisement

import dagger.Component
import kz.sozdik.advertisement.presentation.AdvertPresenter
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter

@Presenter
@Component(
    modules = [AdvertModule::class],
    dependencies = [AppDependency::class]
)
interface AdvertPresenterComponent {
    fun getAdvertPresenter(): AdvertPresenter
}