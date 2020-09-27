package kz.sozdik.favorites.data

import kz.sozdik.core.db.dao.WordDao
import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.core.network.provider.TokenProvider
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.favorites.data.api.FavoriteApi
import kz.sozdik.favorites.domain.FavoritesRepository
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteApi: FavoriteApi,
    private val wordDao: WordDao,
    private val tokenProvider: TokenProvider,
) : FavoritesRepository {

    override suspend fun getFavorites(langFrom: String): List<Word> {
        if (isAuthorized()) {
            val words = favoriteApi.loadFavorites(langFrom).data
            wordDao.insert(words)
        }
        return wordDao.getFavoriteWords(langFrom)
    }

    override suspend fun clearFavorites(langFrom: String) {
        favoriteApi.clearFavorites(langFrom)
        wordDao.deleteFavoriteWords(langFrom)
    }

    override suspend fun getFavoriteWordsCount(): Int = wordDao.favoriteWordsCount()

    // TODO: Сделать сразу обновление без вызова метода getWord()
    override suspend fun createFavoritePhrase(word: Word): Word =
        if (isAuthorized()) {
            val response = favoriteApi.createFavoritePhrase(word.langFrom, word.langTo, word.phrase)
            if (response.result == ResponseWrapper.RESULT_OK) {
                val word = response.data
                wordDao.getWord(word.phrase, word.langFrom, word.langTo)
                word.favourite = 1
                wordDao.update(word)
                word
            } else {
                throw IllegalStateException("Unable to create favorite word")
            }
        } else {
            val word = wordDao.getWord(word.phrase, word.langFrom, word.langTo)
            if (word != null) {
                word.favourite = 1
                wordDao.update(word)
            }
            word!!
        }

    override suspend fun deleteFavoritePhrase(word: Word): Word =
        if (isAuthorized()) {
            val response = favoriteApi.deleteFavoritePhrase(word.langFrom, word.langTo, word.phrase)
            if (response.result == ResponseWrapper.RESULT_OK) {
                val word = response.data
                val localWord = wordDao.getWord(word.phrase, word.langFrom, word.langTo)!!
                localWord.favourite = 0
                wordDao.update(localWord)
                localWord
            } else {
                throw IllegalStateException("Unable to delete favorite word")
            }
        } else {
            val word = wordDao.getWord(word.phrase, word.langFrom, word.langTo)
            if (word != null) {
                word.favourite = 0
                wordDao.update(word)
            }
            word!!
        }

    private fun isAuthorized(): Boolean = tokenProvider.token != null
}