package kz.sozdik.advertisement.presentation

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.launch
import kz.sozdik.BuildConfig
import kz.sozdik.R
import kz.sozdik.advertisement.domain.AdvertisementInteractor
import kz.sozdik.base.BaseMvpPresenter
import kz.sozdik.main.AdvertView
import kz.sozdik.main.AdvertView.AdProvider
import moxy.InjectViewState
import timber.log.Timber
import javax.inject.Inject

private const val AD_PROVIDER_KEY = "android_ad_provider"

@InjectViewState
class AdvertPresenter @Inject constructor(
    private var firebaseRemoteConfig: FirebaseRemoteConfig?,
    private val advertisementInteractor: AdvertisementInteractor
) : BaseMvpPresenter<AdvertView>() {

    private var adProvider = AdProvider.UNKNOWN

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance().apply {
            setDefaultsAsync(R.xml.remote_config_defaults)
            if (BuildConfig.DEBUG) {
                setConfigSettingsAsync(
                    FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(0)
                        .build()
                )
            }
        }
    }

    fun setIsAdDisabled(isDisabled: Boolean) {
        if (isDisabled) {
            adProvider = AdProvider.DISABLED_BY_PURCHASE
            viewState.hideAllAdViews()
        } else {
            fetchRemoteConfig()
        }
    }

    fun onAdLoaded(provider: AdProvider) {
        viewState.hideAllAdViews()
        viewState.changeAdViewVisibility(provider, true)
    }

    private fun fetchRemoteConfig() {
        if (adProvider == AdProvider.DISABLED_BY_PURCHASE) {
            return
        }

        val cacheExpiration =
            firebaseRemoteConfig?.info?.configSettings?.minimumFetchIntervalInSeconds ?: 0
        firebaseRemoteConfig?.fetch(cacheExpiration)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("Firebase remote config fetched successfully")

                    if (adProvider == AdProvider.DISABLED_BY_DEVELOPER) {
                        return@addOnCompleteListener
                    }
                    adProvider = try {
                        val adProviderKey =
                            firebaseRemoteConfig?.getString(AD_PROVIDER_KEY).orEmpty().uppercase()
                        AdProvider.valueOf(adProviderKey)
                    } catch (e: IllegalArgumentException) {
                        AdProvider.UNKNOWN
                    }
                    Timber.d("AdProvider is $adProvider")
                    when (adProvider) {
                        AdProvider.CUSTOM -> loadCustomBanner()
                        else -> viewState.hideAllAdViews()
                    }
                } else {
                    Timber.e(task.exception, "Firebase remote config fetch failed")

                    adProvider = AdProvider.UNKNOWN
                    viewState.hideAllAdViews()
                }
            }
    }

    private fun loadCustomBanner() {
        launch {
            try {
                val banner = advertisementInteractor.loadBanner()
                viewState.showCustomAdView(banner)
            } catch (e: Throwable) {
                Timber.e(e, "Unable to load custom banner")
            }
        }
    }
}