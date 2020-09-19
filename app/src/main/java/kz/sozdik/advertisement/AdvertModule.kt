package kz.sozdik.advertisement

import dagger.Binds
import dagger.Module
import dagger.Provides
import kz.sozdik.advertisement.data.AdvertisementRestGateway
import kz.sozdik.advertisement.data.api.AdvertisementApi
import kz.sozdik.advertisement.domain.AdvertisementRemoteGateway
import retrofit2.Retrofit

@Module
abstract class AdvertModule {

    @Binds
    abstract fun bindBannerRepository(repository: AdvertisementRestGateway): AdvertisementRemoteGateway

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideApi(retrofit: Retrofit): AdvertisementApi =
            retrofit.create(AdvertisementApi::class.java)
    }
}