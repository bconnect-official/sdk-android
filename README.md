Le SDK Android pour BConnect est un SDK vous permettant d'implémenter simplement un connection BConnect dans votre application. 
Son fonctionnement repose en grande partie sur la [librairie AppAuth](https://github.com/openid/AppAuth-Android/tree/master) et à travers elle, suit les bonnes pratiques établies dans le  [RFC 8252 - OAuth 2.0 for Native Apps](https://tools.ietf.org/html/rfc8252) incluant l'utilisation des [Custom Tabs](https://developer.chrome.com/multidevice/android/customtabs).

## Téléchargements
Le SDK Android pour BConnect est disponible sur [MavenCentral](https://search.maven.org/search?q=g:net.bconnect.sdk)

```groovy
implementation 'net.bconnect.sdk:library:<version>'
```

## Requirements
Le SDK Android pour BConnect supporte un version minimale d'Android 16 (Jellybean) à la compilation, mais son fonctionnement étant basé sur les AppLinks, le bouton de connexion ne sera affiché qu'à partir d'Android 10 (minSDK 29). Le bouton sera simplement masqué sur les versions antérieures.
Pour fonctionner, le SDK aura également besoin que vous ayez reçu de la part des équipes BConnect un `client-id` ainsi qu'un uri de redirection sans lequel il ne sera pas possible d'effectuer le parcours de connexion.

## Ajout du placeholder pour la redirection AppAuth
Une fois la connexion terminée, la librairie AppAuth utilisée par le SDK BConnect a besoin d'un placeholder vers lequel rediriger avec les informations de connexions. C'est ici qu'il faudra ajouter votre redirect URI dans votre manifest. 
⚠️Le redirect uri doit être enregistré auprès des équipes BConnect
```xml
<activity
    android:name="net.openid.appauth.RedirectUriReceiverActivity"
    android:exported="true"
    tools:node="replace">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:host="<le host sur lequel vous voulez recevoir la redirection>"
            android:scheme="<votre scheme>" />
    </intent-filter>
</activity>
```

## Intégration du bouton BConnect
Le bouton BConnect est disponible soit en version Jetpack compose, soit en version XML. Il n'y a aucune différence dans son fonctionnement entre les deux versions.

# Version Jetpack Compose
Pour intégrer le bouton, rajoutez simplement le composable suivant : 
```kotlin
BConnectButton(
    clientId = "<votre clientId>",
    redirectUri = "<votre redirectUri>",
)
```

# Version XML
Pour intégrer le bouton, rajouter la vue suivante : 
```xml
<com.cb.bconnectsdk.BConnectButtonView
        android:layout_width="..."
        android:layout_height="..."
        app:clientId="votre clientId"
        app:redirectUri="votre redirectUri" />
```

# Configuration

Que ce soit dans la version Compose ou dans la version XML, le bouton BConnect peut être paramétré à l'aide de plusieurs variables (toutes optionnelles) : 
 - style (défaut : ICON): pour changer son style d'affichage. Vous aurez le choix entre ICON, STANDARD, CONNECT, IDENTIFY et CREATE
 - scope (défaut : "openId") : la liste des scopes qui seront présent dans l'id token à la fin du parcours de connexion, un sous ensemble de ["openid", "email", "given_name", "family_name", "name", "risk_score"]
 - activeAuthentification (défaut : false): un booléen indiquant si l'on veut forcer ou non une authentification banque

Chacune de ces variables est utilisée dans l'application de démonstration pour des exemples plus précis.

## Récupération des données pour finaliser la connexion
Une fois que l'utilisateur se sera connecté à BConnect, avec ou sans connexion banque, l'activité vers laquelle vous avez déclaré votre Deeplink sera rouverte.
Dans la methode `onCreate` vous pourrez utiliser le SDK pour y recevoir les différentes données dont vous aurez besoin pour finaliser la connexion : 
```kotlin
val tokenRequestObject = BConnectIntentParser.parseAuthorizationCodeIntent(intent)
```

## Application de démonstration

Ce repository contient également une application de démonstration, dans laquelle vous aurez simplement à rentrer votre configuration (le `client-id`, et le `redirect-uri` notamment). 

