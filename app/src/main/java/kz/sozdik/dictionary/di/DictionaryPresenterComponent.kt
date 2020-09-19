package kz.sozdik.dictionary.di

import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.dictionary.presentation.DictionaryFragment
import kz.sozdik.dictionary.presentation.DictionaryPresenter

@Presenter
@Component(
    modules = [DictionaryModule::class],
    dependencies = [AppDependency::class]
)
interface DictionaryPresenterComponent {
    fun getDictionaryPresenter(): DictionaryPresenter
    fun inject(fragment: DictionaryFragment)
}