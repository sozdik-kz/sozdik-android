package kz.sozdik.feedback.di

import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.feedback.presentation.FeedbackPresenter

@Presenter
@Component(
    modules = [FeedbackModule::class],
    dependencies = [AppDependency::class]
)
interface FeedbackPresenterComponent {
    fun getFeedbackPresenter(): FeedbackPresenter
}