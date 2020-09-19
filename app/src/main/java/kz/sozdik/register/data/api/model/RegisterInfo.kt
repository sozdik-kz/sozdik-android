package kz.sozdik.register.data.api.model

import com.google.gson.annotations.SerializedName

data class RegisterInfo(
    @SerializedName("email")
    val email: String,
    @SerializedName("confirm_token")
    val confirmToken: String
)