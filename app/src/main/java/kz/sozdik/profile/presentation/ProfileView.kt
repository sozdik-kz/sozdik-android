package kz.sozdik.profile.presentation

import kz.sozdik.profile.domain.model.Profile
import moxy.MvpView

interface ProfileView : MvpView {
    fun showLoginAndRegisterButtons(isVisible: Boolean)
    fun showProfileInfo(isVisible: Boolean)
    fun setProfileInfo(profile: Profile)
}