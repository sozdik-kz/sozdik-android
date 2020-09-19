package kz.sozdik.advertisement.data

import kz.sozdik.advertisement.data.api.AdvertisementApi
import kz.sozdik.advertisement.domain.AdvertisementRemoteGateway
import kz.sozdik.advertisement.domain.model.Banner
import javax.inject.Inject

class AdvertisementRestGateway @Inject constructor(
    private val advertisementApi: AdvertisementApi
) : AdvertisementRemoteGateway {

    override suspend fun loadBanner(): Banner {
        val bannerInfo = advertisementApi.loadBanner("M-320x50").data
        return Banner(bannerInfo.urlImage, bannerInfo.urlLink, bannerInfo.title)
    }
}