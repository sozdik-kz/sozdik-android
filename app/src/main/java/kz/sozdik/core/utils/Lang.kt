package kz.sozdik.core.utils

object Lang {
    const val KAZAKH_CYRILLIC = "kk"
    const val KAZAKH_ROMAN = "kk-Latn"
    const val RUSSIAN = "ru"

    fun isKazakh(lang: String): Boolean = lang == KAZAKH_CYRILLIC || lang == KAZAKH_ROMAN
}