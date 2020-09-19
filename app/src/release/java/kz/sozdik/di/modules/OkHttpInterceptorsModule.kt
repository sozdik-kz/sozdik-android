package kz.sozdik.di.modules

import java.util.List
import java.util.ArrayList
import dagger.Module
import dagger.Provides
import kz.sozdik.di.qualifiers.OkHttpInterceptors
import kz.sozdik.di.qualifiers.OkHttpNetworkInterceptors
import okhttp3.Interceptor

@Module
object OkHttpInterceptorsModule {
    @Provides
    @OkHttpInterceptors
    @JvmStatic
    fun provideOkHttpInterceptors(): List<Interceptor> = ArrayList()

    @Provides
    @OkHttpNetworkInterceptors
    @JvmStatic
    fun provideOkHttpNetworkInterceptors(): List<Interceptor> = ArrayList()
}