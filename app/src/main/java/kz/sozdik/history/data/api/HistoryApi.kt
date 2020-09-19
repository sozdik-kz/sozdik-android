package kz.sozdik.history.data.api

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.dictionary.domain.model.Word
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface HistoryApi {
    @FormUrlEncoded
    @POST("api/")
    suspend fun loadHistory(
        @Field("api_method") apiMethod: String = "user.history.read"
    ): ResponseWrapper<List<Word>>

    @FormUrlEncoded
    @POST("api/")
    suspend fun clearHistory(
        @Field("lang_from") langFrom: String,
        @Field("api_method") apiMethod: String = "user.history.delete"
    )

    @FormUrlEncoded
    @POST("api/")
    suspend fun deleteWordFromHistory(
        @Field("lang_from") langFrom: String,
        @Field("lang_to") langTo: String,
        @Field("phrase") phrase: String,
        @Field("api_method") apiMethod: String = "user.history.delete"
    )
}