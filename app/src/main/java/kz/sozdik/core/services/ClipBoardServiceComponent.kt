package kz.sozdik.core.services

import dagger.Component
import kz.sozdik.core.di.AppDependency

@Component(
    dependencies = [AppDependency::class]
)
interface ClipBoardServiceComponent {
    fun inject(service: ClipboardService)
}