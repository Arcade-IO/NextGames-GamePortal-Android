// ui/userpage/UserPage.kt
package dk.nextgames.app.ui.userpage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.nextgames.app.viewModel.ProfileViewModel
import dk.nextgames.app.ui.pages.PageScaffold

@Composable
fun UserPage(onBack: () -> Unit, vm: ProfileViewModel = viewModel()) {
    val state = vm.uiState

    PageScaffold(title = "Profile", onBack = onBack) { inner ->
        when {
            state.loading -> Box(
                Modifier.padding(inner).fillMaxSize(),
                Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Box(
                Modifier.padding(inner).fillMaxSize(),
                Alignment.Center
            ) { Text(state.error) }

            state.profile != null -> {
                Column(
                    Modifier
                        .padding(inner)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // “Kasse” = temaets container (surfaceVariant) med global opacity
                    Surface(
                        color        = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape        = MaterialTheme.shapes.medium
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Username:", style = MaterialTheme.typography.labelLarge)
                            Text(state.profile.name.ifBlank { "NO VALUE" })
                            Spacer(Modifier.height(8.dp))
                            Text("Email:", style = MaterialTheme.typography.labelLarge)
                            Text(state.profile.email.ifBlank { "UNKNOWN" })
                        }
                    }
                }
            }
        }
    }
}
