package kz.sozdik.login.data.api.model

import com.google.gson.annotations.SerializedName

data class LoginInfo(
    @SerializedName("auth_token")
    val authToken: String?
)