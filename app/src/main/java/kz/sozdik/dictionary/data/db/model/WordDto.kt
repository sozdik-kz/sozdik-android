package kz.sozdik.dictionary.data.db.model

import androidx.room.Entity
import kz.sozdik.dictionary.domain.model.Word

@Entity(tableName = "words", primaryKeys = ["phrase", "langFrom"])
data class WordDto(
    val phrase: String,
    val langFrom: String,
    val langTo: String,
    val phraseAcute: String?,
    val phraseIpa: String?,
    val translation: String?,
    val favourite: Int = 0,
    val audioHash: String?,
    val urlShort: String?,
    val similarPhrases: List<String>?,
    val synonyms: List<String>?,
    val translatedAt: Long,
)

fun WordDto.toWord(): Word =
    Word(
        phrase,
        langFrom,
        langTo,
        phraseAcute,
        phraseIpa,
        translation,
        favourite == 1,
        audioHash,
        urlShort,
        similarPhrases,
        synonyms,
    )