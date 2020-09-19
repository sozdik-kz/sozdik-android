package kz.sozdik.profile.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kz.sozdik.profile.domain.model.Profile

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles")
    suspend fun getProfile(): Profile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile)

    @Query("DELETE FROM profiles")
    suspend fun delete()
}