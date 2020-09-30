package kz.sozdik.dictionary.data.api

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.dictionary.data.api.model.SuggestionsInfo
import kz.sozdik.dictionary.data.api.model.WordInfo
import kz.sozdik.dictionary.domain.model.Audio
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DictionaryApi {
    @FormUrlEncoded
    @POST("api/")
    @Suppress("LongParameterList")
    suspend fun translate(
        @Field("lang_from") langFrom: String,
        @Field("lang_to") langTo: String,
        @Field("phrase") phrase: String,
        @Field("output_samples") outputSamples: Int = 1,
        @Field("output_links") outputLinks: Int = 1,
        @Field("api_method") apiMethod: String = "dictionary.article.read"
    ): ResponseWrapper<WordInfo>

    @FormUrlEncoded
    @POST("api/")
    suspend fun loadSuggestions(
        @Field("lang_from") langFrom: String,
        @Field("lang_to") langTo: String,
        @Field("phrase") phrase: String,
        @Field("api_method") apiMethod: String = "dictionary.suggests.read"
    ): ResponseWrapper<SuggestionsInfo>

    @FormUrlEncoded
    @POST("api/")
    suspend fun getAudio(
        @Field("audio_hash") audioHash: String,
        @Field("api_method") apiMethod: String = "dictionary.audio.read"
    ): ResponseWrapper<Audio>
}