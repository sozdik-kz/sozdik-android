package kz.sozdik.advertisement.domain

import kz.sozdik.advertisement.domain.model.Banner
import javax.inject.Inject

class AdvertisementInteractor @Inject constructor(
    private val advertisementRemoteGateway: AdvertisementRemoteGateway
) {

    suspend fun loadBanner(): Banner =
        advertisementRemoteGateway.loadBanner()
}