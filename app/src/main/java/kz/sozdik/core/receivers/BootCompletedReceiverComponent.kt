package kz.sozdik.core.receivers

import dagger.Component
import kz.sozdik.core.di.AppDependency

@Component(
    dependencies = [AppDependency::class]
)
interface BootCompletedReceiverComponent {
    fun inject(receiver: BootCompletedReceiver)
}