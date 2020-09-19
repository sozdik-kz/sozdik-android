package kz.sozdik.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import kz.sozdik.App
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.modules.ApplicationModule
import kz.sozdik.di.modules.DataModule
import kz.sozdik.di.modules.NetworkModule
import kz.sozdik.di.modules.OkHttpInterceptorsModule
import kz.sozdik.dictionary.di.MediaModule
import kz.sozdik.favorites.di.FavoriteModule
import kz.sozdik.history.di.HistoryModule
import kz.sozdik.profile.di.ProfileModule
import kz.sozdik.register.di.RegistrationModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        MediaModule::class,
        DataModule::class,
        NetworkModule::class,
        OkHttpInterceptorsModule::class,
        RegistrationModule::class,
        HistoryModule::class,
        ProfileModule::class,
        FavoriteModule::class
    ]
)
interface ApplicationComponent : AppDependency {

    fun inject(app: App)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): ApplicationComponent
    }
}