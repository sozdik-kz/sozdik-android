package kz.sozdik.translation.domain

import javax.inject.Inject

class TranslateInteractor @Inject constructor(
    private val translateRepository: TranslateRemoteGateway
) {
    suspend fun translate(langFrom: String, langTo: String, text: String): String =
        translateRepository.translate(langFrom, langTo, text)
}