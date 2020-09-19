package kz.sozdik.dictionary.data

import kz.sozdik.core.db.dao.WordDao
import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.dictionary.data.api.DictionaryApi
import kz.sozdik.dictionary.domain.DictionaryRepository
import kz.sozdik.dictionary.domain.model.TranslatePhraseResult
import kz.sozdik.dictionary.domain.model.Word
import timber.log.Timber
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val dictionaryApi: DictionaryApi,
    private val wordDao: WordDao
) : DictionaryRepository {

    override suspend fun translate(langFrom: String, langTo: String, phrase: String): TranslatePhraseResult {
        val localWord = wordDao.getWord(phrase, langFrom, langTo)
        val word = if (localWord == null) {
            Timber.e("No word in the local storage, switching to remote data source")
            loadWordFromServerAndSave(langFrom, langTo, phrase)
        } else {
            localWord
        }

        return if (word != null) {
            TranslatePhraseResult.Success(word)
        } else {
            TranslatePhraseResult.WrongPhrase
        }
    }

    private suspend fun loadWordFromServerAndSave(
        langFrom: String,
        langTo: String,
        phrase: String
    ): Word? {
        val response = dictionaryApi.translate(langFrom, langTo, phrase)
        return if (response.result == ResponseWrapper.RESULT_OK) {
            val word = response.data
            Timber.i("$word is loaded from the server")
            word.translatedAt = System.currentTimeMillis()
            wordDao.insertOrUpdate(word)
            word
        } else {
            null
        }
    }
}