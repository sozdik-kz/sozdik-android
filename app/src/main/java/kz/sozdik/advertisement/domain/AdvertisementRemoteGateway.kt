package kz.sozdik.advertisement.domain

import kz.sozdik.advertisement.domain.model.Banner

interface AdvertisementRemoteGateway {
    suspend fun loadBanner(): Banner
}