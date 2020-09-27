package kz.sozdik.profile.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kz.sozdik.profile.data.api.ProfileApi
import kz.sozdik.profile.data.gateway.ProfileDbGateway
import kz.sozdik.profile.data.gateway.ProfileRestGateway
import kz.sozdik.profile.domain.ProfileLocalGateway
import kz.sozdik.profile.domain.ProfileRemoteGateway
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileRemoteGateway(repository: ProfileRestGateway): ProfileRemoteGateway

    @Binds
    @Singleton
    abstract fun bindProfileLocalGateway(repository: ProfileDbGateway): ProfileLocalGateway

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideProfileApi(retrofit: Retrofit): ProfileApi =
            retrofit.create(ProfileApi::class.java)
    }
}