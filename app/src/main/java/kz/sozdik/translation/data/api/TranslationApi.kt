package kz.sozdik.translation.data.api

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.translation.domain.Translate
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TranslationApi {
    @FormUrlEncoded
    @POST("api/")
    suspend fun translateSentence(
        @Field("lang_from") langFrom: String,
        @Field("lang_to") langTo: String,
        @Field("text") text: String,
        @Field("api_method") apiMethod: String = "translator.read"
    ): ResponseWrapper<Translate>
}