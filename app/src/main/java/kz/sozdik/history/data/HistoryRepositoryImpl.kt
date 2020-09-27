package kz.sozdik.history.data

import kz.sozdik.core.db.dao.WordDao
import kz.sozdik.core.network.provider.TokenProvider
import kz.sozdik.core.utils.Lang
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.history.data.api.HistoryApi
import kz.sozdik.history.domain.HistoryRepository
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyApi: HistoryApi,
    private val wordDao: WordDao,
    private val tokenProvider: TokenProvider,
) : HistoryRepository {

    override suspend fun getHistory(langFrom: String): List<Word> {
        if (tokenProvider.token != null) {
            val words = historyApi.loadHistory().data
            wordDao.insert(words)
        }
        return getLocalHistory(langFrom)
    }

    private suspend fun getLocalHistory(langFrom: String): List<Word> =
        if (Lang.isKazakh(langFrom)) {
            wordDao.getKazakhWords()
        } else {
            wordDao.getRussianWords()
        }

    override suspend fun getWordsCount(): Int = wordDao.wordsCount()

    override suspend fun clearHistory(langFrom: String) {
        historyApi.clearHistory(langFrom)
        wordDao.deleteHistory()
    }

    override suspend fun removeWord(word: Word) {
        historyApi.deleteWordFromHistory(
            word.langFrom,
            word.langTo,
            word.phrase
        )
        wordDao.deleteWord(word.phrase, word.langFrom, word.langTo)
    }

    override suspend fun clearAllWords() {
        wordDao.deleteAllWords()
    }
}