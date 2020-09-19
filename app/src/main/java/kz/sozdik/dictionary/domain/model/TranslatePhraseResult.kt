package kz.sozdik.dictionary.domain.model

sealed class TranslatePhraseResult {
    class Success(val word: Word) : TranslatePhraseResult()
    object WrongPhrase : TranslatePhraseResult()
}