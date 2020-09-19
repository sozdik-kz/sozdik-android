package kz.sozdik.feedback.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import kz.sozdik.feedback.data.FeedbackRestGateway
import kz.sozdik.feedback.data.api.FeedbackApi
import kz.sozdik.feedback.domain.FeedbackRemoteGateway
import retrofit2.Retrofit

@Module
abstract class FeedbackModule {

    @Binds
    abstract fun bindFeedbackRepository(repository: FeedbackRestGateway): FeedbackRemoteGateway

    @Module
    companion object {
        @Provides
        @JvmStatic
        fun provideFeedbackApi(retrofit: Retrofit): FeedbackApi =
            retrofit.create(FeedbackApi::class.java)
    }
}