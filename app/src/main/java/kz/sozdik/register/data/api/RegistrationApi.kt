package kz.sozdik.register.data.api

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.register.data.api.model.ConfirmCodeInfo
import kz.sozdik.register.data.api.model.RegisterInfo
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RegistrationApi {
    @FormUrlEncoded
    @POST("api/")
    suspend fun register(
        @Field("device_token") pushToken: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("timezone") timezone: Long,
        @Field("api_method") apiMethod: String = "user.auth.create"
    ): ResponseWrapper<RegisterInfo>

    @FormUrlEncoded
    @POST("api/")
    suspend fun confirmCode(
        @Field("confirm_code") confirmCode: String,
        @Field("confirm_token") confirmToken: String,
        @Field("api_method") apiMethod: String = "user.confirm.read"
    ): ResponseWrapper<ConfirmCodeInfo>
}