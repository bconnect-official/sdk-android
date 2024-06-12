package net.bconnect.sdk

import android.content.Intent

object BConnectIntentParser {
    fun parseAuthorizationCodeIntent(intent: Intent?): TokenRequestObject? {
        if (intent == null) return null
        val uriFromIntent = intent.data
        val params = uriFromIntent?.queryParameterNames?.associate {
            it to uriFromIntent.getQueryParameter(it)
        }
        return if (!params.isNullOrEmpty() && (params["code"] as String?) != null) {
            TokenRequestObject(
                authorizationCode = params["code"] as String,
                codeChallenge = codeVerifier!!,
                state = params["state"]
            )
        } else {
            null
        }
    }
}

data class TokenRequestObject(
    val authorizationCode: String,
    val codeChallenge: String,
    val state: String?
)