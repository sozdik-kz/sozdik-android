package kz.sozdik.dictionary.domain

import kz.sozdik.dictionary.domain.gateway.AudioRemoteGateway
import kz.sozdik.dictionary.domain.gateway.SuggestionsRemoteGateway
import kz.sozdik.dictionary.domain.model.Audio
import kz.sozdik.dictionary.domain.model.SuggestionsResult
import kz.sozdik.dictionary.domain.model.TranslatePhraseResult
import javax.inject.Inject

class DictionaryInteractor @Inject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val suggestionsRemoteGateway: SuggestionsRemoteGateway,
    private val audioRemoteGateway: AudioRemoteGateway
) {

    suspend fun translate(langFrom: String, langTo: String, phrase: String): TranslatePhraseResult =
        dictionaryRepository.translate(langFrom, langTo, phrase)

    suspend fun getSuggestions(
        langFrom: String,
        langTo: String,
        phrase: String
    ): SuggestionsResult =
        suggestionsRemoteGateway.getSuggestions(langFrom, langTo, phrase)

    suspend fun getAudio(audioHash: String): Audio = audioRemoteGateway.load(audioHash)
}