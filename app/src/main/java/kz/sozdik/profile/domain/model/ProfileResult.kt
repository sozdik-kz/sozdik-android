package kz.sozdik.profile.domain.model

sealed class ProfileResult {
    class Success(val profile: Profile) : ProfileResult()
    class Error(val code: Int) : ProfileResult()
}