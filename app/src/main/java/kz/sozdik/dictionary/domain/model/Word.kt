package kz.sozdik.dictionary.domain.model

import androidx.room.Entity
import org.jsoup.Jsoup
import java.io.Serializable

private const val HREF_LEADING = "<a href=\"/dictionary/translate"
private const val HREF_TRAILING = "</a>"
private const val ABBR_TAG = "<abbr>син.</abbr> "
private const val HREF_REGEXP = "(<a>)(.*?)(</a>)"

@Entity(tableName = "words", primaryKeys = ["phrase", "langFrom"])
class Word(
    var phrase: String,
    var langFrom: String,
    var langTo: String,
    var translatedAt: Long
) : Serializable {

    var phraseAcute: String? = null

    var phraseIpa: String? = null

    var translation: String? = null

    var favourite: Int = 0

    var audioHash: String? = null

    var urlShort: String? = null

    var similarPhrases: List<String>? = null

    var synonyms: List<String>? = null

    val translateAsText: String
        get() = Jsoup.parse(translation).text()

    val similarPhrasesAsHtml: String
        get() {
            return similarPhrases?.joinToString { getHref(it) }.orEmpty()
        }

    val isFavorite: Boolean
        get() = favourite == 1

    // TODO: Should be used only in presentation layer
    val isRight: Boolean
        get() = !translation.isNullOrEmpty()

    fun hasAudio(): Boolean = !audioHash.isNullOrEmpty()

    fun getFormattedTranslation(): String? {
        val stringBuilder = StringBuilder()
        if (!translation.isNullOrEmpty()) {
            val translationAsHtml = translation?.replace(
                HREF_REGEXP.toRegex(),
                """$HREF_LEADING/$langTo/$langFrom/$2">$2$HREF_TRAILING"""
            )
            if (synonyms.isNullOrEmpty()) {
                stringBuilder.append(translationAsHtml)
            } else {
                stringBuilder
                    .append(translationAsHtml?.replace("</p>", "<br>"))
                    .append(ABBR_TAG)
                    .append(synonyms?.joinToString { getHref(it) })
                    .append("</p>")
            }
        }
        return stringBuilder.toString()
    }

    override fun toString(): String = phrase

    fun isSameWord(w: Word?): Boolean =
        w != null && phrase == w.phrase && langFrom == w.langFrom && langTo == w.langTo

    private fun getHref(phrase: String): String =
        """$HREF_LEADING/$langFrom/$langTo/$phrase">$phrase$HREF_TRAILING"""
}