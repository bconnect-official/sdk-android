package net.bconnect.sdk

import android.content.Context
import android.net.Uri
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

internal object BConnectServiceHelper {

    private var serviceConfig: AuthorizationServiceConfiguration? = null
    private var discoveryUrl: String? = null

    fun initAuthService(discoveryUrl: String?) {
        if (serviceConfig == null || discoveryUrl != this.discoveryUrl) {
            this.discoveryUrl = discoveryUrl
            AuthorizationServiceConfiguration.fetchFromUrl(
                Uri.parse(discoveryUrl)
            ) { serviceConfiguration, _ ->
                if (serviceConfiguration != null) serviceConfig = serviceConfiguration
            }
        }
    }

    fun launchBConnectLogin(
        context: Context,
        authUrl: String?,
        tokenUrl: String?,
        clientId: String,
        redirectUri: String,
        scope: List<BConnectScope>,
        activeAuthentification: Boolean,
    ) {
        val appAuthConfig = AppAuthConfiguration.Builder().build()
        val authService = AuthorizationService(context, appAuthConfig)
        val serviceConfig = this.serviceConfig ?: AuthorizationServiceConfiguration(
            Uri.parse(authUrl),
            Uri.parse(tokenUrl)
        )
        val authRequestBuilder = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            Uri.parse(redirectUri)
        ).setScope(scope.joinToString(" ") { it.toParamString() })
        val map = mutableMapOf<String, String>()
        if (activeAuthentification) {
            map["acr_values"] = "https://api.bconnect.net/sso/v2/oauth2/bconnect/acr/active"
        }
        map["sdk_type"] = "android"
        map["sdk_version"] = "1.0"
        authRequestBuilder.setAdditionalParameters(map)
        val authRequest = authRequestBuilder.build()
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        codeVerifier = authRequest.codeVerifier
        context.startActivity(authIntent)
    }
}