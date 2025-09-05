// ui/login/LoginScreen.kt
package dk.nextgames.app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.nextgames.app.R
import dk.nextgames.app.auth.LoginViewModel
import dk.nextgames.app.helper.AppOutlinedTextField

@Composable
fun LoginScreen(
    vm: LoginViewModel = viewModel(),
    onLoggedIn: () -> Unit,
    onForgotPassword: () -> Unit
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
                Surface(
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Logo
                        Image(
                            painter = painterResource(R.drawable.ic_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .fillMaxHeight(0.35f)
                                .aspectRatio(1f)
                        )
                        Spacer(Modifier.height(24.dp))

                        Text("Log dig ind", style = MaterialTheme.typography.titleLarge)

                        Spacer(Modifier.height(24.dp))

                        AppOutlinedTextField(
                            value = ui.email,
                            onValueChange = vm::onEmailChange,
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        AppOutlinedTextField(
                            value = ui.password,
                            onValueChange = vm::onPasswordChange,
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = { vm.login(onLoggedIn) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Log ind")
                        }

                        // 🔹 Forgot password button
                        TextButton(
                            onClick = onForgotPassword,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Forgot password?")
                        }

                        Spacer(Modifier.height(24.dp))

                        ui.error?.let {
                            Spacer(Modifier.height(8.dp))
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
