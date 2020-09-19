package kz.sozdik.profile.data.gateway

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.profile.data.api.ProfileApi
import kz.sozdik.profile.domain.ProfileRemoteGateway
import kz.sozdik.profile.domain.model.ProfileResult
import javax.inject.Inject

class ProfileRestGateway @Inject constructor(
    private val profileApi: ProfileApi
) : ProfileRemoteGateway {

    override suspend fun loadProfile(): ProfileResult {
        val response = profileApi.loadProfile()
        return when (response.result) {
            ResponseWrapper.RESULT_OK -> ProfileResult.Success(response.data)
            else -> ProfileResult.Error(response.result)
        }
    }
}