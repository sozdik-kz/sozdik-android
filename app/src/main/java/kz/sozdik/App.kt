package kz.sozdik

import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import com.pixplicity.easyprefs.library.Prefs
import kz.sozdik.core.di.AppDependency
import kz.sozdik.di.ApplicationComponent
import kz.sozdik.di.DaggerApplicationComponent
import timber.log.Timber

class App : MultiDexApplication(), AppDependency.AppDepsProvider {

    private val appComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        appComponent.inject(this)

        Fresco.initialize(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setupEasyPrefs()
    }

    override fun provideAppDependency(): AppDependency = appComponent

    private fun setupEasyPrefs() {
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}