package kz.sozdik.dictionary.data

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.dictionary.data.api.DictionaryApi
import kz.sozdik.dictionary.data.api.model.toWordDto
import kz.sozdik.dictionary.data.api.model.toWordEntity
import kz.sozdik.dictionary.data.db.WordDao
import kz.sozdik.dictionary.data.db.model.toWord
import kz.sozdik.dictionary.domain.DictionaryRepository
import kz.sozdik.dictionary.domain.model.TranslatePhraseResult
import timber.log.Timber
import javax.inject.Inject

private const val SUCCESS_TRANSLATION_LANGUAGE_INVERTED = 2

class DictionaryRepositoryImpl @Inject constructor(
    private val dictionaryApi: DictionaryApi,
    private val wordDao: WordDao,
) : DictionaryRepository {

    override suspend fun translate(langFrom: String, langTo: String, phrase: String): TranslatePhraseResult {
        val wordDto = wordDao.getWord(phrase, langFrom, langTo)
        return if (wordDto == null || wordDto.translation.isNullOrEmpty()) {
            Timber.i("No word in the local storage, switching to remote data source")
            loadWordFromServerAndSave(langFrom, langTo, phrase)
        } else {
            Timber.i("Translation found in the local storage")
            TranslatePhraseResult.Success(wordDto.toWord())
        }
    }

    private suspend fun loadWordFromServerAndSave(
        langFrom: String,
        langTo: String,
        phrase: String,
    ): TranslatePhraseResult {
        val response = dictionaryApi.translate(langFrom, langTo, phrase)
        return if (
            response.result == ResponseWrapper.RESULT_OK ||
            response.result == SUCCESS_TRANSLATION_LANGUAGE_INVERTED
        ) {
            val wordInfo = response.data
            Timber.i("$wordInfo is loaded from the server")
            wordDao.insertOrUpdate(wordInfo.toWordDto())
            TranslatePhraseResult.Success(wordInfo.toWordEntity())
        } else {
            TranslatePhraseResult.WrongPhrase
        }
    }
}