package kz.sozdik.dictionary.data.api.model

import com.google.gson.annotations.SerializedName

data class SuggestionsInfo(
    @SerializedName("phrase")
    val phrase: String? = null,
    @SerializedName("lang_from")
    val langFrom: String? = null,
    @SerializedName("lang_to")
    val langTo: String? = null,
    @SerializedName("total_found")
    val totalFound: Int = 0,
    @SerializedName("suggests")
    val suggestions: List<String>? = null
)