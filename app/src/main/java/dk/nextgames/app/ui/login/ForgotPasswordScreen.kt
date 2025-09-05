package dk.nextgames.app.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.nextgames.app.helper.AppOutlinedTextField
import dk.nextgames.app.auth.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(
    vm: ForgotPasswordViewModel = viewModel(),
    onBack: () -> Unit
) {
    val ui by vm.ui.collectAsState()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (ui.isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text("Reset Password", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))

                    AppOutlinedTextField(
                        value = ui.email,
                        onValueChange = vm::onEmailChange,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { vm.resetPassword() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send reset link")
                    }

                    ui.message?.let {
                        Spacer(Modifier.height(12.dp))
                        Text(it, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(Modifier.height(16.dp))

                    TextButton(onClick = onBack) {
                        Text("Back to login")
                    }
                }
            }
        }
    }
}
