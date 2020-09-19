package kz.sozdik.profile.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import kz.sozdik.profile.data.api.ProfileApi
import kz.sozdik.profile.data.gateway.ProfileDbGateway
import kz.sozdik.profile.data.gateway.ProfileRestGateway
import kz.sozdik.profile.domain.ProfileLocalGateway
import kz.sozdik.profile.domain.ProfileRemoteGateway
import retrofit2.Retrofit

@Module
abstract class ProfileModule {

    @Binds
    abstract fun bindProfileRemoteGateway(repository: ProfileRestGateway): ProfileRemoteGateway

    @Binds
    abstract fun bindProfileLocalGateway(repository: ProfileDbGateway): ProfileLocalGateway

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideProfileApi(retrofit: Retrofit): ProfileApi =
            retrofit.create(ProfileApi::class.java)
    }
}