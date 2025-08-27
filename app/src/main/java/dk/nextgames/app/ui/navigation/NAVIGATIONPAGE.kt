// ui/navigation/NAVIGATIONPAGE.kt
package dk.nextgames.app.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dk.nextgames.app.R
import dk.nextgames.app.navigation.Routes

// Ramme/streger (uændret)
private val OutlineColor = Color.Black
private val DividerColor = Color.Black
private val OutlineWidth = 6.dp
private val DividerWidth = 6.dp

@Composable
fun NAVIGATIONPAGE(
    onNavigate: (String) -> Unit,
    onLogout:   () -> Unit
) {
    // Brug altid temaets container (med global opacity fra theme)
    val cellColor = MaterialTheme.colorScheme.surfaceVariant

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ØVERSTE HALVDEL
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val tint = LocalContentColor.current
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_signout),
                        contentDescription = "Log ud",
                        tint = tint
                    )
                }

                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = "App logo",
                    contentScale = ContentScale.FillBounds,
                    modifier          = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight(0.75f)
                        .aspectRatio(1f)
                )
            }

            // NEDERSTE HALVDEL: 2×2 grid til kanten
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .border(OutlineWidth, OutlineColor)
                    .padding(OutlineWidth)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        GridCell(
                            label = "profile",
                            iconResId = R.drawable.signup,
                            cellBg   = cellColor,
                            onClick  = { onNavigate(Routes.USER) }
                        )
                        GridCell(
                            label = "settings",
                            iconResId = R.drawable.settings,
                            cellBg   = cellColor,
                            onClick  = { onNavigate(Routes.SETTINGS) }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        GridCell(
                            label = "games",
                            iconResId = R.drawable.game,
                            cellBg   = cellColor,
                            onClick  = { onNavigate(Routes.GAMES) }
                        )
                        GridCell(
                            label = "leaderboard",
                            iconResId = R.drawable.podium,
                            cellBg   = cellColor,
                            onClick  = { onNavigate(Routes.LEADERBOARD) }
                        )
                    }
                }

                // Midterlinjer
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight()
                        .width(DividerWidth)
                        .background(DividerColor)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(DividerWidth)
                        .background(DividerColor)
                )
            }
        }
    }
}

@Composable
private fun RowScope.GridCell(
    label: String,
    @DrawableRes iconResId: Int,
    cellBg: Color,
    onClick: () -> Unit
) {
    val tint = LocalContentColor.current
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(cellBg)              // ← følger temaets surfaceVariant (m. opacity)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(iconResId),
                contentDescription = label,
                modifier = Modifier.size(56.dp),
                colorFilter = ColorFilter.tint(tint)
            )
            Text(text = label, style = MaterialTheme.typography.titleMedium)
        }
    }
}
