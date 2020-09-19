package kz.sozdik.history.domain

import kz.sozdik.dictionary.domain.model.Word

interface HistoryRepository {
    suspend fun getHistory(langFrom: String): List<Word>
    suspend fun getWordsCount(): Int
    suspend fun clearHistory(langFrom: String)
    suspend fun removeWord(word: Word)
    suspend fun clearAllWords()
}