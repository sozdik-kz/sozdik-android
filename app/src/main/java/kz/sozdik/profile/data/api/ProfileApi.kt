package kz.sozdik.profile.data.api

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.profile.domain.model.Profile
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ProfileApi {

    @FormUrlEncoded
    @POST("api/")
    suspend fun loadProfile(
        @Field("api_method") apiMethod: String = "user.data.read"
    ): ResponseWrapper<Profile>
}