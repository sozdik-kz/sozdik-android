package kz.sozdik.translation.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import kz.sozdik.translation.data.TranslateRestGateway
import kz.sozdik.translation.data.api.TranslationApi
import kz.sozdik.translation.domain.TranslateRemoteGateway
import retrofit2.Retrofit

@Module
abstract class TranslateModule {

    @Binds
    abstract fun bindTranslateRepository(repository: TranslateRestGateway): TranslateRemoteGateway

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideTranslationApi(retrofit: Retrofit): TranslationApi =
            retrofit.create(TranslationApi::class.java)
    }
}