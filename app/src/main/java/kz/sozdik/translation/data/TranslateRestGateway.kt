package kz.sozdik.translation.data

import kz.sozdik.translation.data.api.TranslationApi
import kz.sozdik.translation.domain.TranslateRemoteGateway
import javax.inject.Inject

class TranslateRestGateway @Inject constructor(
    private val translationApi: TranslationApi
) : TranslateRemoteGateway {

    override suspend fun translate(
        langFrom: String,
        langTo: String,
        text: String
    ): String {
        val response = translationApi.translateSentence(langFrom, langTo, text)
        return response.data.text
    }
}