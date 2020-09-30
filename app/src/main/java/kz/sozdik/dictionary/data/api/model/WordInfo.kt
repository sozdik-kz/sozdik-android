package kz.sozdik.dictionary.data.api.model

import com.google.gson.annotations.SerializedName
import kz.sozdik.dictionary.data.db.model.WordDto
import kz.sozdik.dictionary.domain.model.Word

data class WordInfo(
    @SerializedName("phrase")
    val phrase: String,
    @SerializedName("lang_from")
    val langFrom: String,
    @SerializedName("lang_to")
    val langTo: String,
    @SerializedName("phrase_acute")
    val phraseAcute: String?,
    @SerializedName("phrase_ipa")
    val phraseIpa: String?,
    @SerializedName("translation")
    val translation: String?,
    @SerializedName("favourite")
    val favourite: Int,
    @SerializedName("audio_hash")
    val audioHash: String?,
    @SerializedName("url_short")
    val shortUrl: String?,
    @SerializedName("similar_phrases")
    val similarPhrases: List<String>?,
    @SerializedName("synonyms")
    val synonyms: List<String>?,
)

fun WordInfo.toWordDto(): WordDto =
    WordDto(
        phrase,
        langFrom,
        langTo,
        phraseAcute,
        phraseIpa,
        translation,
        favourite,
        audioHash,
        shortUrl,
        similarPhrases,
        synonyms,
        System.currentTimeMillis()
    )

fun WordInfo.toWordEntity(): Word =
    Word(
        phrase,
        langFrom,
        langTo,
        phraseAcute,
        phraseIpa,
        translation,
        favourite == 1,
        audioHash,
        shortUrl,
        similarPhrases,
        synonyms,
    )