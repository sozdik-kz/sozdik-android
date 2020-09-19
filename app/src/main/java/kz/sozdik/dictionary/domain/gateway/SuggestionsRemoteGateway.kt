package kz.sozdik.dictionary.domain.gateway

import kz.sozdik.dictionary.domain.model.SuggestionsResult

interface SuggestionsRemoteGateway {
    suspend fun getSuggestions(
        langFrom: String,
        langTo: String,
        phrase: String
    ): SuggestionsResult
}