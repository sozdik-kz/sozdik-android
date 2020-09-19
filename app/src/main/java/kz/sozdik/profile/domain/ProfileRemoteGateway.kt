package kz.sozdik.profile.domain

import kz.sozdik.profile.domain.model.ProfileResult

interface ProfileRemoteGateway {
    suspend fun loadProfile(): ProfileResult
}