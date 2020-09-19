package kz.sozdik.main

import dagger.Component
import kz.sozdik.core.di.AppDependency

@Component(
    dependencies = [AppDependency::class]
)
interface MainActivityComponent {
    fun inject(activity: MainActivity)
}