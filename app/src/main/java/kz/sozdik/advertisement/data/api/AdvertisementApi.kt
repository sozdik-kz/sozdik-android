package kz.sozdik.advertisement.data.api

import kz.sozdik.advertisement.data.api.model.BannerInfo
import kz.sozdik.core.models.ResponseWrapper
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AdvertisementApi {
    @FormUrlEncoded
    @POST("api/")
    suspend fun loadBanner(
        @Field("code") bannerCode: String,
        @Field("api_method") apiMethod: String = "advertising.read"
    ): ResponseWrapper<BannerInfo>
}