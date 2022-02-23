package kz.sozdik.di.modules

import android.content.ClipboardManager
import android.content.Context
import android.preference.PreferenceManager
import com.akexorcist.localizationactivity.core.LocalizationContext
import dagger.Module
import dagger.Provides
import kz.sozdik.core.system.PrefsManager

@Module
object ApplicationModule {

    @Provides
    fun provideApplicationContext(context: Context): LocalizationContext = LocalizationContext(context)

    @Provides
    fun provideSharedPreferences(context: Context): PrefsManager =
        PrefsManager(PreferenceManager.getDefaultSharedPreferences(context))

    @Provides
    fun provideClipboardManager(context: Context): ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}