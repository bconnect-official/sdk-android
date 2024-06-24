package net.bconnect.sdk

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Scale

internal var codeVerifier: String? = null
private const val AUTH_URL = "https://bconnect.net/a/oauth2/authorize"
private const val TOKEN_URL = "https://api.b.connect.net/sso/v2/oauth2/bconnect/access_token"
private const val DISCOVERY_URL = "https://api.bconnect.net/sso/v2/oauth2/bconnect/.well-known/openid-configuration"

@Composable
fun BConnectButton(
    clientId: String,
    redirectUri: String,
    modifier: Modifier = Modifier,
    authUrl: String? = AUTH_URL,
    tokenUrl: String? = TOKEN_URL,
    discoveryUrl: String? = DISCOVERY_URL,
    style: BConnectButtonStyle = BConnectButtonStyle.ICON,
    activeAuthentification: Boolean = false,
    scope: List<BConnectScope> = listOf(BConnectScope.OPEN_ID)
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        Box {}
        return
    }
    BConnectServiceHelper.initAuthService(discoveryUrl)
    val context = LocalContext.current
    Box(modifier = modifier
        .clickable {
            BConnectServiceHelper.launchBConnectLogin(
                context = context,
                authUrl = authUrl,
                tokenUrl = tokenUrl,
                clientId = clientId,
                redirectUri = redirectUri,
                scope = scope,
                activeAuthentification = activeAuthentification,
            )
        }
        .semantics(mergeDescendants = true) {}) {
        val imageUrl = style.getImageUrl()
        val contentDescription = "Se connecter avec B Connect"
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .decoderFactory(SvgDecoder.Factory())
                .data(imageUrl)
                .scale(scale = Scale.FIT)
                .build()
        )
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxWidth().fillMaxHeight()
        )
    }
}

enum class BConnectButtonStyle {
    ICON,
    BPREMIER_BLUE,
    BPREMIER_WHITE,
    LARGE_BLACK,
    LARGE_BLUE,
    LARGE_WHITE,
    LARGE_WHITE_BW,
    LARGE_WHITE_NO_BORDER,
    LARGE_WHITE_NO_BORDER_BW,
    LOGOTYPE_BLUE,
    LOGOTYPE_BLUE_BW,
    LOGOTYPE_WHITE,
    LOGOTYPE_WHITE_BW,
    SIGNATURE_BLACK,
    SIGNATURE_BLUE,
    SIGNATURE_ORANGE,
    SIGNATURE_WHITE;

    fun getImageUrl() : String {
        return when (this) {
            ICON -> "https://www.bconnect.net/sdk/bconnect_button.svg"
            LARGE_WHITE -> "https://www.bconnect.net/sdk/bconnect_largebutton_white.svg"
            BPREMIER_BLUE -> "https://www.bconnect.net/sdk/bconnect_bpremier_blue.svg"
            BPREMIER_WHITE -> "https://www.bconnect.net/sdk/bconnect_bpremier_white.svg"
            LARGE_BLACK -> "https://www.bconnect.net/sdk/bconnect_largebutton_black.svg"
            LARGE_BLUE -> "https://www.bconnect.net/sdk/bconnect_largebutton_blue.svg"
            LARGE_WHITE_BW -> "https://www.bconnect.net/sdk/bconnect_largebutton_white_bw.svg"
            LARGE_WHITE_NO_BORDER -> "https://www.bconnect.net/sdk/bconnect_largebutton_white_noborder.svg"
            LARGE_WHITE_NO_BORDER_BW -> "https://www.bconnect.net/sdk/bconnect_largebutton_white_noborder_bw.svg"
            LOGOTYPE_BLUE -> "https://www.bconnect.net/sdk/bconnect_logotype_blue.svg"
            LOGOTYPE_BLUE_BW -> "https://www.bconnect.net/sdk/bconnect_logotype_blue_bw.svg"
            LOGOTYPE_WHITE -> "https://www.bconnect.net/sdk/bconnect_logotype_white.svg"
            LOGOTYPE_WHITE_BW -> "https://www.bconnect.net/sdk/bconnect_logotype_white_bw.svg"
            SIGNATURE_BLACK -> "https://www.bconnect.net/sdk/bconnect_signature_black.svg"
            SIGNATURE_BLUE -> "https://www.bconnect.net/sdk/bconnect_signature_blue.svg"
            SIGNATURE_ORANGE -> "https://www.bconnect.net/sdk/bconnect_signature_orange.svg"
            SIGNATURE_WHITE -> "https://www.bconnect.net/sdk/bconnect_signature_white.svg"
        }
    }

    companion object {
        internal fun fromInt(value: Int): BConnectButtonStyle {
            return when (value) {
                1 -> BPREMIER_BLUE
                2 -> BPREMIER_WHITE
                3 -> LARGE_BLACK
                4 -> LARGE_BLUE
                5 -> LARGE_WHITE
                6 -> LARGE_WHITE_BW
                7 -> LARGE_WHITE_NO_BORDER
                8 -> LARGE_WHITE_NO_BORDER_BW
                9 -> LOGOTYPE_BLUE
                10 -> LOGOTYPE_BLUE_BW
                11 -> LOGOTYPE_WHITE
                12 -> LOGOTYPE_WHITE_BW
                13 -> SIGNATURE_BLACK
                14 -> SIGNATURE_BLUE
                15 -> SIGNATURE_ORANGE
                16 -> SIGNATURE_WHITE
                else -> ICON
            }
        }
    }
}

enum class BConnectScope {
    OPEN_ID,
    EMAIL,
    GIVEN_NAME,
    FAMILY_NAME,
    NAME,
    RISK_SCORE;

    internal fun toParamString(): String {
        return when (this) {
            OPEN_ID -> "openid"
            EMAIL -> "email"
            GIVEN_NAME -> "given_name"
            FAMILY_NAME -> "family_name"
            NAME -> "name"
            RISK_SCORE -> "risk_score"
        }
    }

    companion object {
        internal fun fromString(value: String): BConnectScope? {
            return when (value) {
                "openid" -> OPEN_ID
                "email" -> EMAIL
                "given_name" -> GIVEN_NAME
                "family_name" -> FAMILY_NAME
                "name" -> NAME
                "risk_score" -> RISK_SCORE
                else -> null
            }
        }
    }
}