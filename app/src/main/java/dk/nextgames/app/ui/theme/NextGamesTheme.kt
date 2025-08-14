// ui/theme/NextGamesTheme.kt
package dk.nextgames.app.ui.theme

import android.annotation.SuppressLint
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import dk.nextgames.app.data.ColorPrefs

// Hjælpe-extension: sort eller hvid afhængig af luminans
private fun Color.contrast(): Color =
    if (luminance() > 0.5f) Color.Black else Color.White

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NextGamesTheme(content: @Composable () -> Unit) {
    // 1) Læs Context
    val ctx = LocalContext.current

    // 2) Hent bg+btn som én StateFlow<Pair<Color,Color>>
    val colorPair by ColorPrefs
        .colors(ctx)
        .collectAsState(initial = ColorPrefs.DefaultBg to ColorPrefs.DefaultBtn)

    // 3) Destructure til to almindelige val’er
    val bgColor = colorPair.first
    val btnColor = colorPair.second

    // 4) Beregn kontrast-farver
    val onBg  = bgColor.contrast()
    val onBtn = btnColor.contrast()

    // 5) Byg dit ColorScheme — sæt surface til bgColor i stedet for Transparent
    val scheme = lightColorScheme(
        background    = Color.Transparent,
        onBackground  = onBg,

        // <- HER sætter vi alle surface-komponenter til bruger-baggrund
        surface       = bgColor,
        onSurface     = onBg,

        // du kan stadig have surfaceVariant, hvis du vil
        surfaceVariant   = bgColor,
        onSurfaceVariant = onBg,

        primary       = btnColor,
        onPrimary     = onBtn
        // tilføj secondary/tertiary om nødvendigt
    )

    // 6) Apply temaet
    MaterialTheme(
        colorScheme = scheme,
        typography  = AppTypography,
        content     = content
    )
}
