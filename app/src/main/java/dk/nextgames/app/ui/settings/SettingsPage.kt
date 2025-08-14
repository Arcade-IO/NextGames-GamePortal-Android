package dk.nextgames.app.ui.settings

/* ---------- imports ---------- */
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.nextgames.app.helper.AdaptiveSurface
import dk.nextgames.app.viewModel.SettingsViewModel
import androidx.core.net.toUri
import com.google.firebase.BuildConfig
import dk.nextgames.app.ui.pages.PageScaffold

/* -------------------------------- */

@Composable
fun SettingsPage(
    onBack: () -> Unit,
    vm: SettingsViewModel = viewModel()   // ← bruger AndroidViewModel ovenfor
) {
    val state by vm.ui.collectAsState()

    PageScaffold("Settings", onBack) { inner ->

        if (state.loading) {
            Box(Modifier.padding(inner).fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@PageScaffold
        }

        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            /* --- Farver -------------------------------------------------- */
            AdaptiveSurface(Color(0xFFEAE7F2), 1.dp) {
                Column(Modifier.padding(16.dp)) {

                    Text("Background Color", style = MaterialTheme.typography.titleLarge)
                    ColorRow(
                        current = state.bgColor,
                        onPick  = vm::setBgColor
                    )

                    Spacer(Modifier.height(16.dp))

                    Text("Button Color", style = MaterialTheme.typography.titleLarge)
                    ColorRow(
                        current = state.btnColor,
                        onPick  = vm::setBtnColor
                    )
                }
            }

            /* --- App-info ------------------------------------------------ */
            AdaptiveSurface(Color(0xFFEAE7F2), 1.dp) {
                Column(Modifier.padding(16.dp)) {
                    Text("Next Games", style = MaterialTheme.typography.titleLarge)
                    Text("Version: 0.0.1")
                    Spacer(Modifier.height(8.dp))
                    Text("This is a retro-inspired game portal.\n"+
                            "Play games and beat your own or a friend’s high-score.")
                }
            }

            /* --- Bug-report --------------------------------------------- */
            BugReportButton()
        }
    }
}

/* ► En vandret række med små farve-cirkler */
@Composable
private fun ColorRow(
    current: Color,
    onPick: (Color) -> Unit
) {
    val presets = listOf(
        Color(0xFF11151C),  // mørk
        Color(0xFFEAE7F2),  // lys
        Color(0xFF2B7E76),  // teal
        Color(0xFFE75A27),  // orange
        Color(0xFFF7B000)   // gul
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        presets.forEach { c ->
            Surface(
                color = c,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onPick(c) },
                tonalElevation = if (c == current) 4.dp else 0.dp
            ) { /* tom – blot en farvecirkel */ }
        }
    }
}

/* ► Bug-report knap */
@Composable
private fun BugReportButton() {
    val ctx = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:gameportaltec@gmail.com".toUri()
                putExtra(Intent.EXTRA_SUBJECT, "Bug-rapport from android application")
            }
            ctx.startActivity(Intent.createChooser(intent, "Send e-mail …"))
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.MailOutline, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Report an error")
    }
}
