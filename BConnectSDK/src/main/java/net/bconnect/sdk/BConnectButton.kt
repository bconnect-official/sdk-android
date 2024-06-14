package net.bconnect.sdk

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        val imageUrl = when (style) {
            BConnectButtonStyle.ICON -> "https://www.bconnect.net/sdk/bconnect_button.svg"
            BConnectButtonStyle.STANDARD -> "https://www.bconnect.net/sdk/bconnect_largebutton_white.svg"
            BConnectButtonStyle.CREATE -> "https://www.bconnect.net/sdk/activate_button_grey.svg"
            BConnectButtonStyle.IDENTIFY -> "https://www.bconnect.net/sdk/bconnect_signature_orange.svg"
            BConnectButtonStyle.CONNECT -> "https://www.bconnect.net/sdk/connect_button_grey.svg"
        }
        val contentDescription = when (style) {
            BConnectButtonStyle.ICON, BConnectButtonStyle.CONNECT, BConnectButtonStyle.STANDARD -> "Se connecter avec B Connect"
            BConnectButtonStyle.CREATE -> "Créer son compte avec B Connect"
            BConnectButtonStyle.IDENTIFY -> "S'identifier avec B Connect"
        }
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
            modifier = Modifier.fillMaxWidth()
        )
    }
}

enum class BConnectButtonStyle {
    ICON,
    STANDARD,
    CONNECT,
    IDENTIFY,
    CREATE;

    companion object {
        internal fun fromInt(value: Int): BConnectButtonStyle {
            return when (value) {
                1 -> STANDARD
                2 -> CONNECT
                3 -> IDENTIFY
                4 -> CREATE
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