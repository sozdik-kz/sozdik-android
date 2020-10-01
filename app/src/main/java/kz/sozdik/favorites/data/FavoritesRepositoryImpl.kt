package kz.sozdik.favorites.data

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.core.network.provider.TokenProvider
import kz.sozdik.dictionary.data.api.model.toWordDto
import kz.sozdik.dictionary.data.db.WordDao
import kz.sozdik.dictionary.data.db.model.toWord
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
            val wordDtoList = favoriteApi.loadFavorites(langFrom).data.map {
                it.toWordDto()
            }
            wordDao.insert(wordDtoList)
        }
        return wordDao.getFavoriteWords(langFrom).map { it.toWord() }
    }

    override suspend fun clearFavorites(langFrom: String) {
        favoriteApi.clearFavorites(langFrom)
        wordDao.deleteFavoriteWords(langFrom)
    }

    override suspend fun getFavoriteWordsCount(): Int = wordDao.favoriteWordsCount()

    override suspend fun inverseFavorite(word: Word): Word =
        if (isAuthorized()) {
            updateOnRemote(word)
        } else {
            updateInDb(word)
        }

    private suspend fun updateOnRemote(word: Word): Word {
        val isFavorite = !word.isFavorite
        val response = if (isFavorite) {
            favoriteApi.createFavoritePhrase(word.langFrom, word.langTo, word.phrase)
        } else {
            favoriteApi.deleteFavoritePhrase(word.langFrom, word.langTo, word.phrase)
        }
        return if (response.result == ResponseWrapper.RESULT_OK) {
            val newFavoriteValue = response.data.favourite
            val wordDto = response.data.toWordDto().copy(favourite = newFavoriteValue)
            wordDao.update(wordDto)
            word.copy(isFavorite = newFavoriteValue == 1)
        } else {
            word
        }
    }

    private suspend fun updateInDb(word: Word): Word {
        val isFavorite = !word.isFavorite
        val wordDto = wordDao.getWord(word.phrase, word.langFrom, word.langTo)
        return if (wordDto != null) {
            val changedRowsCount = wordDao.update(wordDto.copy(favourite = if (isFavorite) 1 else 0))
            if (changedRowsCount == 0) {
                word
            } else {
                word.copy(isFavorite = isFavorite)
            }
        } else {
            word
        }
    }

    private fun isAuthorized(): Boolean = tokenProvider.token != null
}