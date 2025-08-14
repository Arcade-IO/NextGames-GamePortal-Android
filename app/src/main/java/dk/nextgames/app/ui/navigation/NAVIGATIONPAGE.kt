package dk.nextgames.app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import dk.nextgames.app.navigation.Routes
import dk.nextgames.app.R

@Composable
fun NAVIGATIONPAGE(
    onNavigate: (String) -> Unit,
    onLogout:   () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logout-knap øverst i højre hjørne
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Log ud"
                    )
                }
            }

            // Dit logo under logout-knappen
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = "App logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(vertical = 16.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Menu-knapperne
            MenuButton("Profile")     { onNavigate(Routes.USER) }
            MenuButton("Settings")    { onNavigate(Routes.SETTINGS) }
            MenuButton("Games")       { onNavigate(Routes.GAMES) }
            MenuButton("Leaderboard") { onNavigate(Routes.LEADERBOARD) }
        }
    }
}

@Composable
private fun MenuButton(text: String, onClick: () -> Unit) {
    androidx.compose.material3.Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Text(text)
    }
}
