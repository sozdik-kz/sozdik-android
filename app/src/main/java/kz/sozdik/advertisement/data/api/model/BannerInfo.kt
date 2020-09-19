package kz.sozdik.advertisement.data.api.model

import com.google.gson.annotations.SerializedName

data class BannerInfo(
    @SerializedName("url_link")
    val urlLink: String,
    @SerializedName("url_image")
    val urlImage: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("width")
    val width: Int
)