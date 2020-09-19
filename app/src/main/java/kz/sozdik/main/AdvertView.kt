package kz.sozdik.main

import kz.sozdik.advertisement.domain.model.Banner
import moxy.MvpView

interface AdvertView : MvpView {

    enum class AdProvider {
        CUSTOM,
        DISABLED_BY_DEVELOPER,
        DISABLED_BY_PURCHASE,
        UNKNOWN
    }

    fun showCustomAdView(banner: Banner)

    fun hideAllAdViews()

    fun changeAdViewVisibility(provider: AdProvider, isVisible: Boolean)
}