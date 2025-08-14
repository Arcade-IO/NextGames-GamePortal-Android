package dk.nextgames.app.ui.gameinterface

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.nextgames.app.ui.pages.PageScaffold
import dk.nextgames.app.viewModel.GameInterfaceViewModel
import java.net.URLDecoder

@Composable
fun GameInterfacePage(
    encodedUrl: String,
    gameId:     String,     // <— send med i nav-argumenter
    gameTitle:  String,
    onBack:     () -> Unit
) {
    val url = remember(encodedUrl) { URLDecoder.decode(encodedUrl, "UTF-8") }
    val vm: GameInterfaceViewModel = viewModel()
    val ctx = LocalContext.current
    PageScaffold(title = gameTitle, onBack = onBack) { padding ->
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}
