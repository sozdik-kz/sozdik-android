package kz.sozdik.dictionary.data.gateway

import kz.sozdik.dictionary.data.api.DictionaryApi
import kz.sozdik.dictionary.domain.gateway.AudioRemoteGateway
import kz.sozdik.dictionary.domain.model.Audio
import javax.inject.Inject

class AudioRestGateway @Inject constructor(
    private val dictionaryApi: DictionaryApi
) : AudioRemoteGateway {

    override suspend fun load(audioHash: String): Audio =
        dictionaryApi.getAudio(audioHash).data
}