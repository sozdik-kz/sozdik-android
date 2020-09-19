package kz.sozdik.dictionary.data.gateway

import kz.sozdik.core.models.ResponseWrapper
import kz.sozdik.dictionary.data.api.DictionaryApi
import kz.sozdik.dictionary.domain.gateway.SuggestionsRemoteGateway
import kz.sozdik.dictionary.domain.model.SuggestionsResult
import kz.sozdik.dictionary.domain.model.SuggestionsResult.NoSuggestions
import kz.sozdik.dictionary.domain.model.SuggestionsResult.Success
import kz.sozdik.dictionary.domain.model.SuggestionsResult.WrongPhrase
import javax.inject.Inject

private const val SUGGESTIONS_NOT_FOUND_CODE = 0

class SuggestionsRestGateway @Inject constructor(
    private val dictionaryApi: DictionaryApi
) : SuggestionsRemoteGateway {

    override suspend fun getSuggestions(
        langFrom: String,
        langTo: String,
        phrase: String
    ): SuggestionsResult {
        val response = dictionaryApi.loadSuggestions(langFrom, langTo, phrase)
        return when (response.result) {
            ResponseWrapper.RESULT_OK -> Success(response.data.suggestions.orEmpty())
            SUGGESTIONS_NOT_FOUND_CODE -> NoSuggestions
            else -> WrongPhrase
        }
    }
}