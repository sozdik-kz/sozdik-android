package kz.sozdik.favorites.domain

import kz.sozdik.dictionary.domain.model.Word
import javax.inject.Inject

class FavoritesInteractor @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {

    suspend fun getFavoriteWordsCount(): Int =
        favoritesRepository.getFavoriteWordsCount()

    suspend fun getFavorites(langFrom: String): List<Word> =
        favoritesRepository.getFavorites(langFrom)

    suspend fun clearFavorites(langFrom: String) =
        favoritesRepository.clearFavorites(langFrom)

    suspend fun inverseFavoritesPhrase(word: Word): Word =
        favoritesRepository.inverseFavorite(word)
}