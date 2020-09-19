package kz.sozdik.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kz.sozdik.core.db.dao.WordDao
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.profile.data.db.ProfileDao
import kz.sozdik.profile.domain.model.Profile

@Database(
    version = 3,
    entities = [Word::class, Profile::class]
)
@TypeConverters(
    Converters::class
)
abstract class SozdikDatabase : RoomDatabase() {
    abstract fun getWordDao(): WordDao
    abstract fun getProfileDao(): ProfileDao
}