package kz.sozdik.history.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import kz.sozdik.history.data.HistoryRepositoryImpl
import kz.sozdik.history.data.api.HistoryApi
import kz.sozdik.history.domain.HistoryRepository
import retrofit2.Retrofit

@Module
abstract class HistoryModule {

    @Binds
    abstract fun bindHistoryRepository(repository: HistoryRepositoryImpl): HistoryRepository

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideHistoryApi(retrofit: Retrofit): HistoryApi =
            retrofit.create(HistoryApi::class.java)
    }
}