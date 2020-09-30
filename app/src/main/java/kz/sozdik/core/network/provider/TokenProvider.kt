package kz.sozdik.core.network.provider

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val KEY_SESSION_TOKEN = "KEY_SESSION_TOKEN"

class TokenProvider @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {

    var token: String? = runBlocking { sharedPreferences.getString(KEY_SESSION_TOKEN, null) }
        set(value) {
            field = value
            runBlocking {
                sharedPreferences.edit(commit = true) {
                    putString(KEY_SESSION_TOKEN, value)
                }
            }
        }
}