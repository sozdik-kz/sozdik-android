package kz.sozdik.core.fcm

import dagger.Component
import kz.sozdik.core.di.AppDependency

@Component(
    dependencies = [AppDependency::class]
)
interface PushServiceComponent {
    fun inject(service: PushService)
}