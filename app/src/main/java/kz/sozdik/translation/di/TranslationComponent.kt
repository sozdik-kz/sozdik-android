package kz.sozdik.translation.di

import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.translation.presentation.TranslationFragment

@Presenter
@Component(
    modules = [TranslateModule::class],
    dependencies = [AppDependency::class]
)
interface TranslationComponent {
    fun inject(fragment: TranslationFragment)
}