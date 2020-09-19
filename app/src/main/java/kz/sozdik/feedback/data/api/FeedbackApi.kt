package kz.sozdik.feedback.data.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FeedbackApi {
    @FormUrlEncoded
    @POST("api/")
    suspend fun postFeedback(
        @Field("device_token") pushToken: String,
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("message") message: String,
        @Field("api_method") apiMethod: String = "user.feedback.create"
    )
}