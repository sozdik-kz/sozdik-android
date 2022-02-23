package kz.sozdik.core.di

import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Vibrator
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kz.sozdik.dictionary.data.db.WordDao
import kz.sozdik.core.system.PrefsManager
import kz.sozdik.favorites.domain.FavoritesRepository
import kz.sozdik.history.domain.HistoryRepository
import kz.sozdik.login.domain.gateway.LoginRemoteGateway
import kz.sozdik.profile.domain.ProfileLocalGateway
import kz.sozdik.profile.domain.ProfileRemoteGateway
import kz.sozdik.register.domain.RegisterRemoteGateway
import retrofit2.Retrofit

/**[AppDependency] will be the base provider for feature modules(project)
 * Will be included as `dependencies = [AppDependency::class]`
 * In dagger we can provide dependencies by interface therefore it is better to use
 * custom base interface and extend this and make it encapsulated
 * instead usage of direct base component(dagger).
 */

interface AppDependency :
    RegistrationDependency,
    ProfileDependency,
    HistoryDependency,
    FavoritesDependency {

    /**Provider interface for [AppDependency]
     * If child component wants to get dependencies from [AppDependency]
     * it must provide [AppDependency]. Therefore this interface must be
     * implemented by `Application or activity`
     */
    interface AppDepsProvider {
        fun provideAppDependency(): AppDependency
    }

    fun provideContext(): Context
    fun provideSharedPreferences(): SharedPreferences
    fun providePrefsManager(): PrefsManager
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig
    fun provideWordDao(): WordDao
    fun provideRetrofit(): Retrofit
    fun provideMediaPlayer(): MediaPlayer
    fun provideVibrator(): Vibrator
    fun provideClipboardManager(): ClipboardManager
}

interface RegistrationDependency {
    fun provideRegisterRemoteGateway(): RegisterRemoteGateway
    fun provideLoginRemoteGateway(): LoginRemoteGateway
}

interface ProfileDependency {
    fun provideProfileRemoteGateway(): ProfileRemoteGateway
    fun provideProfileLocalGateway(): ProfileLocalGateway
}

interface HistoryDependency {
    fun provideHistoryRepository(): HistoryRepository
}

interface FavoritesDependency {
    fun provideFavoritesRepository(): FavoritesRepository
}