package kz.sozdik.profile.presentation

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.logout.LogoutInteractor
import kz.sozdik.profile.domain.ProfileInteractor
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

@InjectViewState
class ProfilePresenter @Inject constructor(
    private val profileInteractor: ProfileInteractor,
    private val logoutInteractor: LogoutInteractor
) : BaseMvpPresenter<ProfileView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        launch {
            profileInteractor.getProfileFlow().collect { profile ->
                if (profile != null) {
                    viewState.setProfileInfo(profile)
                }
                viewState.showProfileInfo(profile != null)
                viewState.showLoginAndRegisterButtons(profile == null)
                viewState.showLogoutButton(profile != null)
            }
        }
    }

    fun logout() {
        launch {
            try {
                logoutInteractor.logout()
            } catch (e: Throwable) {
                Timber.e(e, "Unable to logout")
            }
        }
    }
}