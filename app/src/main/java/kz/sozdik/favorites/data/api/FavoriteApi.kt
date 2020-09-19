package kz.sozdik.favorites.data.api

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.dictionary.domain.model.Word
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FavoriteApi {
    @FormUrlEncoded
    @POST("api/")
    suspend fun loadFavorites(
        @Field("lang_from") langFrom: String,
        @Field("api_method") apiMethod: String = "user.favourites.phrase.read"
    ): ResponseWrapper<List<Word>>

    @FormUrlEncoded
    @POST("api/")
    suspend fun clearFavorites(
        @Field("lang_from") langFrom: String,
        @Field("api_method") apiMethod: String = "user.favourites.phrase.delete"
    )

    @FormUrlEncoded
    @POST("api/")
    suspend fun createFavoritePhrase(
        @Field("lang_from") langFrom: String,
        @Field("lang_to") langTo: String,
        @Field("phrase") phrase: String,
        @Field("api_method") apiMethod: String = "user.favourites.phrase.create"
    ): ResponseWrapper<Word>

    @FormUrlEncoded
    @POST("api/")
    suspend fun deleteFavoritePhrase(
        @Field("lang_from") langFrom: String,
        @Field("lang_to") langTo: String,
        @Field("phrase") phrase: String,
        @Field("strict") strict: Int = 1,
        @Field("api_method") apiMethod: String = "user.favourites.phrase.delete"
    ): ResponseWrapper<Word>
}