package kz.sozdik.register.data.api.model

import com.google.gson.annotations.SerializedName

data class ConfirmCodeInfo(
    @SerializedName("auth_token")
    val authToken: String?
)