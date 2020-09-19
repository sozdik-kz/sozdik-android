package kz.sozdik.profile.domain

import kotlinx.coroutines.flow.Flow
import kz.sozdik.profile.domain.model.Profile
import kz.sozdik.profile.domain.model.ProfileResult
import javax.inject.Inject

class ProfileInteractor @Inject constructor(
    private val profileRemoteGateway: ProfileRemoteGateway,
    private val profileLocalGateway: ProfileLocalGateway
) {

    suspend fun loadProfile(): ProfileResult {
        // Need to show profile info, if will be error while fetching profile from BE
        profileLocalGateway.setupProfileFromLocalStorage()
        val result = profileRemoteGateway.loadProfile()
        if (result is ProfileResult.Success) {
            profileLocalGateway.setProfile(result.profile)
        }
        return result
    }

    fun getProfileFlow(): Flow<Profile?> = profileLocalGateway.getProfileFlow()

    suspend fun awaitProfile(): Profile = profileLocalGateway.awaitProfile()
}