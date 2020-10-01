package kz.sozdik.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kz.sozdik.dictionary.data.db.WordDao
import kz.sozdik.dictionary.data.db.model.WordDto
import kz.sozdik.profile.data.db.ProfileDao
import kz.sozdik.profile.domain.model.Profile

@Database(
    version = 3,
    entities = [WordDto::class, Profile::class]
)
@TypeConverters(
    Converters::class
)
abstract class SozdikDatabase : RoomDatabase() {
    abstract fun getWordDao(): WordDao
    abstract fun getProfileDao(): ProfileDao
}