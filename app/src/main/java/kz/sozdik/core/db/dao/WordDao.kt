package kz.sozdik.core.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kz.sozdik.core.utils.Lang
import kz.sozdik.dictionary.domain.model.Word

@Dao
@Suppress("TooManyFunctions")
interface WordDao {

    @Query("SELECT COUNT(*) FROM words WHERE favourite = 0")
    suspend fun wordsCount(): Int

    @Query("SELECT COUNT(*) FROM words WHERE favourite = 1")
    suspend fun favoriteWordsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(word: Word)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(words: List<Word>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(word: Word): Int

    @Query("DELETE FROM words WHERE favourite = 0")
    suspend fun deleteHistory(): Int

    @Query("DELETE FROM words")
    suspend fun deleteAllWords(): Int

    @Query("DELETE FROM words WHERE phrase = :phrase AND langFrom = :langFrom AND langTo = :langTo")
    suspend fun deleteWord(phrase: String, langFrom: String, langTo: String)

    @Query("""SELECT * FROM words WHERE langFrom='${Lang.RUSSIAN}' ORDER BY translatedAt DESC""")
    suspend fun getRussianWords(): List<Word>

    @Query(
        """
        SELECT * FROM words
        WHERE langFrom='${Lang.KAZAKH_CYRILLIC}' OR langFrom='${Lang.KAZAKH_ROMAN}'
        ORDER BY translatedAt DESC
        """
    )
    suspend fun getKazakhWords(): List<Word>

    // region Favorites
    @Query("SELECT * FROM words WHERE langFrom=:langFrom AND favourite = 1 ORDER BY phrase ASC")
    suspend fun getFavoriteWords(langFrom: String): List<Word>

    @Query("DELETE FROM words WHERE langFrom=:langFrom AND favourite = 1")
    suspend fun deleteFavoriteWords(langFrom: String): Int
    // endregion

    @Query("SELECT * FROM words WHERE phrase=:phrase AND langFrom=:langFrom AND langTo=:langTo")
    suspend fun getWord(phrase: String, langFrom: String, langTo: String): Word?
}