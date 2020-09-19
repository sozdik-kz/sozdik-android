package kz.sozdik.profile.data.gateway

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kz.sozdik.profile.data.db.ProfileDao
import kz.sozdik.profile.domain.ProfileLocalGateway
import kz.sozdik.profile.domain.model.Profile
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ProfileDbGateway @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileLocalGateway {

    private val profileFlow = MutableStateFlow<Profile?>(null)

    override suspend fun setProfile(profile: Profile) {
        profileFlow.value = profile
        profileDao.insert(profile)
    }

    override suspend fun setupProfileFromLocalStorage() {
        profileFlow.value = profileDao.getProfile()
    }

    override fun getProfileFlow(): Flow<Profile?> = profileFlow

    override suspend fun awaitProfile(): Profile = getProfileFlow().filterNotNull().first()

    override suspend fun deleteProfile() {
        profileFlow.value = null
        profileDao.delete()
    }
}