package kz.sozdik.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.room.Room
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import kz.sozdik.core.db.SozdikDatabase
import kz.sozdik.dictionary.data.db.WordDao
import kz.sozdik.profile.data.db.ProfileDao

@Module
object DataModule {

    @Provides
    @JvmStatic
    fun provideSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @JvmStatic
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    @Provides
    @JvmStatic
    fun provideWordDao(database: SozdikDatabase): WordDao = database.getWordDao()

    @Provides
    @JvmStatic
    fun provideProfileDao(database: SozdikDatabase): ProfileDao = database.getProfileDao()

    @Provides
    @JvmStatic
    fun provideSozdikDatabase(context: Context): SozdikDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            SozdikDatabase::class.java, "sozdik3.db"
        )
            .fallbackToDestructiveMigration()
            .build()
}