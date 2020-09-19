package kz.sozdik.dictionary.domain.gateway

import kz.sozdik.dictionary.domain.model.Audio

interface AudioRemoteGateway {
    suspend fun load(audioHash: String): Audio
}