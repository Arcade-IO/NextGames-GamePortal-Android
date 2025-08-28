package dk.nextgames.app.ui.gameinterface

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.nextgames.app.ui.chat.ChatPopup
import dk.nextgames.app.viewModel.ChatViewModel
import dk.nextgames.app.ui.pages.PageScaffold
import dk.nextgames.app.viewModel.GameInterfaceViewModel
import java.net.URLDecoder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun GameInterfacePage(
    encodedUrl: String,
    gameId:     String,
    gameTitle:  String,
    onBack:     () -> Unit
) {
    val url = remember(encodedUrl) { URLDecoder.decode(encodedUrl, "UTF-8") }
    val vm: GameInterfaceViewModel = viewModel()
    val ctx = LocalContext.current

    // State for showing/hiding the chat popup
    val showChat = remember { mutableStateOf(false) }
    // The chat repository, manages all chat communication with Firebase
    val chatViewModel = remember { ChatViewModel(FirebaseDatabase.getInstance()) }
    // Get current user from Firebase Auth for chat userName
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userName = firebaseUser?.displayName ?: firebaseUser?.email ?: "Anonymous"

    PageScaffold(
        title = gameTitle,
        onBack = onBack,
        actions = {
            // Chat icon button in the top app bar, opens the chat popup on click
            IconButton(onClick = { showChat.value = true }) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat")
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                factory = {
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess   = true
                        webViewClient   = WebViewClient()
                        webChromeClient = WebChromeClient()

                        // ----------  BROEN  ----------
                        addJavascriptInterface(
                            GameBridge(vm, gameId, gameTitle),
                            "AndroidBridge"        // ← navnet JS skal bruge
                        )

                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Show chat popup if showChat is true
            if (showChat.value) {
                ChatPopup(
                    gameId = gameId,
                    userName = userName,
                    chatViewModel = chatViewModel,
                    onClose = { showChat.value = false }
                )
            }
        }
    }
}