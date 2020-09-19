package kz.sozdik.dictionary.domain

import kz.sozdik.dictionary.domain.model.TranslatePhraseResult

interface DictionaryRepository {
    suspend fun translate(langFrom: String, langTo: String, phrase: String): TranslatePhraseResult
}