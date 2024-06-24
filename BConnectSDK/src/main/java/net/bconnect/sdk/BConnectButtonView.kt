package net.bconnect.sdk

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import net.bconnect.sdk.BConnectScope.*


class BConnectButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {
    private val authUrl: String
    private val discoveryUrl: String
    private val tokenUrl: String
    private val clientId: String
    private val redirectUri: String
    private val style: BConnectButtonStyle
    private val scope: List<BConnectScope>
    private val activeAuthentification: Boolean

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BConnectButtonView,
            0, 0
        ).apply {
            try {
                authUrl = getString(R.styleable.BConnectButtonView_authUrl)
                    ?: "https://bconnect.net/a/oauth2/authorize"
                discoveryUrl = getString(R.styleable.BConnectButtonView_discoveryUrl)
                    ?: "https://api.bconnect.net/sso/v2/oauth2/bconnect/.well-known/openid-configuration"
                tokenUrl = getString(R.styleable.BConnectButtonView_tokenUrl)
                    ?: "hhttps://api.b.connect.net/sso/v2/oauth2/bconnect/access_token"
                clientId = getString(R.styleable.BConnectButtonView_clientId)
                    ?: throw IllegalArgumentException("Client Id not provided")
                redirectUri = getString(R.styleable.BConnectButtonView_redirectUri)
                    ?: throw IllegalArgumentException("Redirect URI not provided")
                style = BConnectButtonStyle.fromInt(getInt(R.styleable.BConnectButtonView_style, 0))
                scope = getString(R.styleable.BConnectButtonView_scope)?.split(" ")
                    ?.mapNotNull { BConnectScope.fromString(it) } ?: listOf(OPEN_ID)
                activeAuthentification =
                    getBoolean(R.styleable.BConnectButtonView_activeAuthentification, false)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val inflatedView = View.inflate(
                        context,
                        R.layout.bconnect_button_icon,
                        this@BConnectButtonView
                    )
                    val image = inflatedView.findViewById<ImageView>(R.id.bconnectImageView)
                    val imageUrl = style.getImageUrl()
                    val contentDescription = "Se connecter avec B Connect"
                    //val contentDescription = when (style) {
                    //    ICON, CONNECT, STANDARD -> "Se connecter avec B Connect"
                    //    CREATE -> "Créer son compte avec B Connect"
                    //    IDENTIFY -> "S'identifier avec B Connect"
                    //}
                    image.contentDescription = contentDescription
                    image.loadUrl(imageUrl)
                    inflatedView.setOnClickListener {
                        BConnectServiceHelper.initAuthService(discoveryUrl = discoveryUrl)
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
                }
            } finally {
                recycle()
            }
        }
    }
}

fun ImageView.loadUrl(url: String) {
    val imageLoader = ImageLoader.Builder(context)
        .components { add(SvgDecoder.Factory()) }
        .build()

    val request = ImageRequest.Builder(context)
        .data(url)
        .target(this)
        .build()

    imageLoader.enqueue(request)
}