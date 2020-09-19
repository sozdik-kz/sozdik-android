package kz.sozdik.dictionary.domain.model

sealed class SuggestionsResult {
    class Success(val suggestions: List<String>) : SuggestionsResult()
    object NoSuggestions : SuggestionsResult()
    object WrongPhrase : SuggestionsResult()
}