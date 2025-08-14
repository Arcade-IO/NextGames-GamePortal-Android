// ui/common/AdaptiveSurface.kt
package dk.nextgames.app.helper

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Color.contrast() : Color =
    if (this.luminance() > 0.5f) Color.Black else Color.White   // >50 % lys ⇒ sort tekst

@Composable
fun AdaptiveSurface(
    color: Color,
    tonalElevation: Dp = 0.dp,
    content: @Composable () -> Unit
) = Surface(
    color         = color,
    contentColor  = color.contrast(),
    tonalElevation = tonalElevation,
    content       = content
)
