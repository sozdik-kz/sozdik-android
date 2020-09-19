package kz.sozdik.history.domain

import kz.sozdik.dictionary.domain.model.Word
import javax.inject.Inject

class HistoryInteractor @Inject constructor(
    private val historyRepository: HistoryRepository
) {

    suspend fun getHistory(langFrom: String): List<Word> =
        historyRepository.getHistory(langFrom)

    suspend fun getWordsCount(): Int =
        historyRepository.getWordsCount()

    suspend fun clearHistory(langFrom: String) =
        historyRepository.clearHistory(langFrom)

    suspend fun removeWord(word: Word) =
        historyRepository.removeWord(word)

    suspend fun clearAllWordsLocally() {
        historyRepository.clearAllWords()
    }
}