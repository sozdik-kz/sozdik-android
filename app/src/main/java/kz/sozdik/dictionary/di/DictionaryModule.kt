package kz.sozdik.dictionary.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import kz.sozdik.dictionary.data.gateway.AudioRestGateway
import kz.sozdik.dictionary.data.DictionaryRepositoryImpl
import kz.sozdik.dictionary.data.gateway.SuggestionsRestGateway
import kz.sozdik.dictionary.data.api.DictionaryApi
import kz.sozdik.dictionary.domain.gateway.AudioRemoteGateway
import kz.sozdik.dictionary.domain.DictionaryRepository
import kz.sozdik.dictionary.domain.gateway.SuggestionsRemoteGateway
import retrofit2.Retrofit

@Module
abstract class DictionaryModule {

    @Binds
    abstract fun bindDictionaryRepository(dictionaryRepositoryImpl: DictionaryRepositoryImpl): DictionaryRepository

    @Binds
    abstract fun bindAudioGateway(repository: AudioRestGateway): AudioRemoteGateway

    @Binds
    abstract fun bindSuggestionsRepository(repository: SuggestionsRestGateway): SuggestionsRemoteGateway

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideDictionaryApi(retrofit: Retrofit): DictionaryApi =
            retrofit.create(DictionaryApi::class.java)
    }
}