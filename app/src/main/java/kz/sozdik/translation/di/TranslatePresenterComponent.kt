package kz.sozdik.translation.di

import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.translation.presentation.TranslateFragment
import kz.sozdik.translation.presentation.TranslatePresenter

@Presenter
@Component(
    modules = [TranslateModule::class],
    dependencies = [AppDependency::class]
)
interface TranslatePresenterComponent {
    fun getTranslatePresenter(): TranslatePresenter
    fun inject(fragment: TranslateFragment)
}