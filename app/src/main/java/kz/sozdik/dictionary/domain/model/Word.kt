package kz.sozdik.dictionary.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.jsoup.Jsoup

private const val HREF_LEADING = "<a href=\"/dictionary/translate"
private const val HREF_TRAILING = "</a>"
private const val ABBR_TAG = "<abbr>син.</abbr> "
private const val HREF_REGEXP = "(<a>)(.*?)(</a>)"

@Parcelize
data class Word(
    val phrase: String,
    val langFrom: String,
    val langTo: String,
    val phraseAcute: String?,
    val phraseIpa: String?,
    val translation: String?,
    val isFavorite: Boolean,
    val audioHash: String?,
    val shortUrl: String?,
    val similarPhrases: List<String>?,
    val synonyms: List<String>?,
) : Parcelable {
    @IgnoredOnParcel
    val isRight: Boolean = !translation.isNullOrEmpty()

    @IgnoredOnParcel
    val hasAudio: Boolean = !audioHash.isNullOrEmpty()

    @IgnoredOnParcel
    val translateAsText: String = Jsoup.parse(translation).text()

    @IgnoredOnParcel
    val similarPhrasesAsHtml: String
        get() {
            return similarPhrases?.joinToString { getHref(it) }.orEmpty()
        }

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

    override fun equals(other: Any?): Boolean {
        val otherWord = other as? Word
        return otherWord != null &&
            phrase == otherWord.phrase &&
            langFrom == otherWord.langFrom &&
            langTo == otherWord.langTo
    }

    private fun getHref(phrase: String): String =
        """$HREF_LEADING/$langFrom/$langTo/$phrase">$phrase$HREF_TRAILING"""
}