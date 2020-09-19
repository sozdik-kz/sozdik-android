package kz.sozdik.presentation.utils

object PhraseUtils {
    fun normalize(phrase: String): String =
        phrase.replace("ə", "ә")
            .replace("Ə", "Ә")
            .replace("ё", "е")
            .replace("Ё", "Е")
            .trim()
}