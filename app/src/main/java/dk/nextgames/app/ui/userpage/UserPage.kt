package dk.nextgames.app.ui.userpage

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dk.nextgames.app.viewModel.ProfileViewModel
import dk.nextgames.app.ui.pages.PageScaffold
import dk.nextgames.app.ui.common.ContainerSurface

@Composable
fun UserPage(onBack: () -> Unit, vm: ProfileViewModel = viewModel()) {
    val state = vm.uiState
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { vm.uploadImageToCloudinary(context, it) }
        }
    )

    PageScaffold(title = "Profile", onBack = onBack) { inner ->
        when {
            state.loading -> Box(
                Modifier.padding(inner).fillMaxSize(),
                Alignment.Center
            ) { CircularProgressIndicator() }

            state.error != null -> Box(
                Modifier.padding(inner).fillMaxSize(),
                Alignment.Center
            ) { Text(state.error ?: "Unknown error") }

            state.profile != null -> {
                Column(
                    Modifier
                        .padding(inner)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = state.profile.photoUrl.ifNullOrBlank {
                            "https://via.placeholder.com/150"
                        },
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(onClick = { launcher.launch("image/*") }) {
                        Text("Change profile picture")
                    }

                    Spacer(Modifier.height(16.dp))

                    ContainerSurface(
                        modifier = Modifier.fillMaxWidth()
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

private fun String?.ifNullOrBlank(default: () -> String): String =
    if (this.isNullOrBlank()) default() else this
