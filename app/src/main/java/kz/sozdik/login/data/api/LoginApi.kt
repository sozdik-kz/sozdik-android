package kz.sozdik.login.data.api

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.login.data.api.model.LoginInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginApi {
    @FormUrlEncoded
    @POST("api/")
    suspend fun login(
        @Field("device_token") pushToken: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("api_method") apiMethod: String = "user.auth.read"
    ): ResponseWrapper<LoginInfo>

    @Suppress("LongParameterList")
    @FormUrlEncoded
    @POST("api/")
    suspend fun socialRegister(
        @Field("device_token") pushToken: String,
        @Field("email") email: String?,
        @Field("avatar_url") avatarUrl: String,
        @Field("gender") gender: String?,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("dob") birthDate: String?,
        @Field("timezone") timezone: Long,
        @Field("social_network") socialNetwork: String,
        @Field("social_network_id") socialNetworkId: String,
        @Field("api_method") apiMethod: String = "user.auth.update"
    ): ResponseWrapper<LoginInfo>
}