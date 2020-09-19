package kz.sozdik.register.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import kz.sozdik.login.data.api.LoginApi
import kz.sozdik.login.data.gateway.LoginRestGateway
import kz.sozdik.login.domain.gateway.LoginRemoteGateway
import kz.sozdik.register.data.api.RegistrationApi
import kz.sozdik.register.data.gateway.RegistrationRestGateway
import kz.sozdik.register.domain.RegisterRemoteGateway
import retrofit2.Retrofit

@Module
abstract class RegistrationModule {

    @Binds
    abstract fun bindRegisterRemoteGateway(repository: RegistrationRestGateway): RegisterRemoteGateway

    @Binds
    abstract fun bindLoginRemoteGateway(repository: LoginRestGateway): LoginRemoteGateway

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideRegistrationApi(retrofit: Retrofit): RegistrationApi =
            retrofit.create(RegistrationApi::class.java)

        @Provides
        @JvmStatic
        fun provideLoginApi(retrofit: Retrofit): LoginApi = retrofit.create(LoginApi::class.java)
    }
}