package kz.sozdik.favorites.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import kz.sozdik.favorites.data.FavoritesRepositoryImpl
import kz.sozdik.favorites.data.api.FavoriteApi
import kz.sozdik.favorites.domain.FavoritesRepository
import retrofit2.Retrofit

@Module
abstract class FavoriteModule {

    @Binds
    abstract fun bindFavoritesRepository(repository: FavoritesRepositoryImpl): FavoritesRepository

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideFavoriteApi(retrofit: Retrofit): FavoriteApi =
            retrofit.create(FavoriteApi::class.java)
    }
}