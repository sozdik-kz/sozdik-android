package kz.sozdik.profile.di

import dagger.Component
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.scopes.Presenter
import kz.sozdik.profile.presentation.DeviceIdDialogFragment
import kz.sozdik.profile.presentation.ProfilePresenter

@Presenter
@Component(dependencies = [AppDependency::class])
interface ProfileComponent {
    fun getProfilePresenter(): ProfilePresenter

    fun inject(fragment: DeviceIdDialogFragment)
}