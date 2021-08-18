package kz.sozdik.feedback.di

import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.feedback.presentation.FeedbackFragment
import kz.sozdik.feedback.presentation.FeedbackViewModel

@Presenter
@Component(
    modules = [FeedbackModule::class],
    dependencies = [AppDependency::class]
)
interface FeedbackComponent {
    fun inject(fragment: FeedbackFragment)
}