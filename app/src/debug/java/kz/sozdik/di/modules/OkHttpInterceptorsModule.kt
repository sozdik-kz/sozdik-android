package kz.sozdik.di.modules

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.Module
import dagger.Provides
import kz.sozdik.di.qualifiers.OkHttpInterceptors
import kz.sozdik.di.qualifiers.OkHttpNetworkInterceptors
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

@Module
object OkHttpInterceptorsModule {

    @Provides
    @OkHttpInterceptors
    @JvmStatic
    fun provideOkHttpInterceptors(gson: Gson): List<Interceptor> {
        val okHttpLogTag = "OkHttp"
        val okHttpLogger = object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                if (!message.startsWith("{") && !message.startsWith("[")) {
                    Timber.tag(okHttpLogTag).d(message)
                    return
                }
                try {
                    val json = JsonParser.parseString(message)
                    Timber.tag(okHttpLogTag).d(gson.toJson(json))
                } catch (e: Throwable) {
                    Timber.tag(okHttpLogTag).d(message)
                }
            }
        }
        val interceptor = HttpLoggingInterceptor(okHttpLogger)
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return listOf(interceptor)
    }

    @Provides
    @OkHttpNetworkInterceptors
    @JvmStatic
    fun provideOkHttpNetworkInterceptors(): List<Interceptor> = listOf(StethoInterceptor())
}