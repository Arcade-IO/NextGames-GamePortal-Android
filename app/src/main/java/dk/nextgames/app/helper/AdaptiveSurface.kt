// helper/AdaptiveSurface.kt
package dk.nextgames.app.helper

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AdaptiveSurface(
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    tonalElevation: Dp = 0.dp,
    content: @Composable () -> Unit
) = Surface(
    color          = color,
    contentColor   = MaterialTheme.colorScheme.onSurfaceVariant,
    tonalElevation = tonalElevation,
    shape          = MaterialTheme.shapes.medium,   // ← nu følger den din radius-slider
    content        = content
)
