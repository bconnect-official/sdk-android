package net.bconnect.demo.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import net.bconnect.demo.app.ui.theme.BConnectAndroidSDKTheme
import net.bconnect.sdk.BConnectButton
import net.bconnect.sdk.BConnectButtonStyle
import net.bconnect.sdk.BConnectIntentParser
import net.bconnect.sdk.BConnectScope.*

private const val DISCOVERY_URL_INTEG =
    "https://api.bconnect-integ.net/sso/v2/oauth2/bconnect/.well-known/openid-configuration"
private const val DISCOVERY_URL_QUALIF =
    "https://api.bconnect-qualif.net/sso/v2/oauth2/bconnect/.well-known/openid-configuration"
private const val DISCOVERY_URL_PROD =
    "https://api.bconnect.net/sso/v2/oauth2/bconnect/.well-known/openid-configuration"

@OptIn(ExperimentalLayoutApi::class)
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bconnectParsedIntent = BConnectIntentParser.parseAuthorizationCodeIntent(intent)
        enableEdgeToEdge()
        setContent {
            var clientId by rememberSaveable { mutableStateOf("") }
            var environmentChoice by rememberSaveable { mutableStateOf(EnvironmentChoice.QUALIF) }
            var redirectUri by rememberSaveable { mutableStateOf("bconnect.test.app://bconnect") }
            var authUrl by rememberSaveable { mutableStateOf(DISCOVERY_URL_QUALIF) }
            var dropdownExpanded by rememberSaveable { mutableStateOf(false) }
            var buttonStyleSelected by rememberSaveable { mutableStateOf(BConnectButtonStyle.CONNECT) }
            var activeAuthentication by rememberSaveable { mutableStateOf(false) }
            var scopeEmail by rememberSaveable { mutableStateOf(false) }
            var scopeGivenName by rememberSaveable { mutableStateOf(false) }
            var scopeFamilyName by rememberSaveable { mutableStateOf(false) }
            var scopeName by rememberSaveable { mutableStateOf(false) }
            var scopeScore by rememberSaveable { mutableStateOf(false) }
            var showSuccessDialog by rememberSaveable { mutableStateOf(true) }
            val icon = if (dropdownExpanded)
                Icons.Filled.KeyboardArrowUp
            else
                Icons.Filled.KeyboardArrowDown
            BConnectAndroidSDKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        TextField(modifier = Modifier
                            .padding(40.dp)
                            .fillMaxWidth(),
                            value = clientId,
                            onValueChange = { clientId = it },
                            label = { Text("ClientId") })
                        TextField(modifier = Modifier
                            .padding(horizontal = 40.dp)
                            .fillMaxWidth(),
                            value = redirectUri,
                            onValueChange = { redirectUri = it },
                            label = { Text("RedirectUri") })
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                                .clickable { dropdownExpanded = !dropdownExpanded }) {
                            Spacer(modifier = Modifier.width(40.dp))
                            Text("BConnectButtonStyle", modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(40.dp))
                            Icon(icon, "contentDescription")
                            Spacer(modifier = Modifier.width(40.dp))
                        }
                        if (dropdownExpanded) Column {
                            DropdownMenuItem(
                                text = { Text("ICON") },
                                onClick = {
                                    buttonStyleSelected =
                                        BConnectButtonStyle.ICON; dropdownExpanded = false;
                                })
                            DropdownMenuItem(
                                text = { Text("CONNECT") },
                                onClick = {
                                    buttonStyleSelected =
                                        BConnectButtonStyle.CONNECT; dropdownExpanded = false;
                                })
                            DropdownMenuItem(
                                text = { Text("STANDARD") },
                                onClick = {
                                    buttonStyleSelected =
                                        BConnectButtonStyle.STANDARD; dropdownExpanded = false;
                                })
                            DropdownMenuItem(
                                text = { Text("IDENTIFY") },
                                onClick = {
                                    buttonStyleSelected =
                                        BConnectButtonStyle.IDENTIFY; dropdownExpanded = false;
                                })
                            DropdownMenuItem(
                                text = { Text("CREATE") },
                                onClick = {
                                    buttonStyleSelected =
                                        BConnectButtonStyle.CREATE; dropdownExpanded = false;
                                })
                        }
                        Spacer(modifier = Modifier.height(60.dp))
                        BConnectButton(
                            clientId = clientId,
                            redirectUri = redirectUri,
                            style = buttonStyleSelected,
                            activeAuthentification = activeAuthentication,
                            discoveryUrl = authUrl,
                            modifier = Modifier
                                .height(90.dp)
                                .width(600.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(horizontal = 40.dp),
                        )
                        Spacer(modifier = Modifier.height(80.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Spacer(modifier = Modifier.width(40.dp))
                            Text("Active Authentication", modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(40.dp))
                            Checkbox(
                                checked = activeAuthentication,
                                onCheckedChange = { activeAuthentication = it }
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
                            EnvironmentChoice.entries.forEachIndexed { index, environment ->
                                SegmentedButton(
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = 3
                                    ),
                                    onClick = {
                                        environmentChoice = environment
                                        authUrl = when (environment) {
                                            EnvironmentChoice.PROD -> DISCOVERY_URL_PROD
                                            EnvironmentChoice.INTEG -> DISCOVERY_URL_INTEG
                                            EnvironmentChoice.QUALIF -> DISCOVERY_URL_QUALIF
                                        }
                                    },
                                    selected = environmentChoice == environment
                                ) {
                                    Text(environment.name)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Text("Scope", modifier = Modifier.padding(start = 40.dp, bottom = 8.dp))
                        FlowRow(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            FilterChip(
                                selected = scopeEmail,
                                onClick = { scopeEmail = !scopeEmail },
                                label = { Text("Email") },
                                modifier = Modifier.padding(horizontal = 4.dp),
                                leadingIcon = if (scopeEmail) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                }
                            )
                            FilterChip(
                                selected = scopeName,
                                onClick = { scopeName = !scopeName },
                                label = { Text("Name") },
                                modifier = Modifier.padding(horizontal = 4.dp),
                                leadingIcon = if (scopeName) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                }
                            )
                            FilterChip(
                                selected = scopeGivenName,
                                onClick = { scopeGivenName = !scopeGivenName },
                                label = { Text("Given name") },
                                modifier = Modifier.padding(horizontal = 4.dp),
                                leadingIcon = if (scopeGivenName) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                }
                            )
                            FilterChip(
                                selected = scopeFamilyName,
                                onClick = { scopeFamilyName = !scopeFamilyName },
                                label = { Text("Family name") },
                                modifier = Modifier.padding(horizontal = 4.dp),
                                leadingIcon = if (scopeFamilyName) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                }
                            )
                            FilterChip(
                                selected = scopeScore,
                                onClick = { scopeScore = !scopeScore },
                                label = { Text("Risk score") },
                                modifier = Modifier.padding(horizontal = 4.dp),
                                leadingIcon = if (scopeScore) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "Done icon",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Button(modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                            onClick = {
                                this@MainActivity.startActivity(
                                    Intent(
                                        this@MainActivity,
                                        XMLActivity::class.java
                                    )
                                )
                            }) {
                            Text("Passer à la version XML")
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                    if (showSuccessDialog && bconnectParsedIntent != null)
                        LoginSuccessDialog(
                            onConfirmation = { showSuccessDialog = false },
                            dialogTitle = "Bravo, vous êtes connectés !",
                            dialogText = "State : ${bconnectParsedIntent.state}\nCode challenge : ${bconnectParsedIntent.codeChallenge}\nAuthorization Code : ${bconnectParsedIntent.authorizationCode}",
                            icon = Icons.Default.Done
                        )
                }
            }
        }
    }
}

@Composable
fun LoginSuccessDialog(
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Fermer")
            }
        },
    )
}

enum class EnvironmentChoice {
    PROD,
    INTEG,
    QUALIF;
}