package kz.sozdik.di.modules

import dagger.Module
import dagger.Provides
import kz.sozdik.core.network.qualifiers.OkHttpInterceptors
import kz.sozdik.core.network.qualifiers.OkHttpNetworkInterceptors
import okhttp3.Interceptor

@Module
object OkHttpInterceptorsModule {
    @Provides
    @OkHttpInterceptors
    @JvmStatic
    fun provideOkHttpInterceptors(): List<Interceptor> = emptyList()

    @Provides
    @OkHttpNetworkInterceptors
    @JvmStatic
    fun provideOkHttpNetworkInterceptors(): List<Interceptor> = emptyList()
}