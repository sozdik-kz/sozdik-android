package kz.sozdik.translation.domain

interface TranslateRemoteGateway {
    suspend fun translate(langFrom: String, langTo: String, text: String): String
}