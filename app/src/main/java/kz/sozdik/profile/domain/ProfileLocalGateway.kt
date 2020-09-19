package kz.sozdik.profile.domain

import kotlinx.coroutines.flow.Flow
import kz.sozdik.profile.domain.model.Profile

interface ProfileLocalGateway {
    suspend fun setProfile(profile: Profile)
    suspend fun setupProfileFromLocalStorage()
    fun getProfileFlow(): Flow<Profile?>
    suspend fun awaitProfile(): Profile
    suspend fun deleteProfile()
}