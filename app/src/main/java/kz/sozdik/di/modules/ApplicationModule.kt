package kz.sozdik.di.modules

import android.content.Context
import android.preference.PreferenceManager
import com.akexorcist.localizationactivity.core.LocalizationContext
import dagger.Module
import dagger.Provides
import kz.sozdik.core.system.PrefsManager

@Module
object ApplicationModule {

    @Provides
    @JvmStatic
    fun provideApplicationContext(context: Context): LocalizationContext = LocalizationContext(context)

    @Provides
    @JvmStatic
    fun provideSharedPreferences(context: Context): PrefsManager =
        PrefsManager(PreferenceManager.getDefaultSharedPreferences(context))
}