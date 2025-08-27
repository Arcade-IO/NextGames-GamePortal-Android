// app/src/main/java/dk/nextgames/app/ui/theme/NextGamesTheme.kt
package dk.nextgames.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import dk.nextgames.app.data.ColorPrefs

private fun Color.contrast(): Color =
    if (luminance() > 0.5f) Color.Black else Color.White

/* Alfa-komposition: fg over bg (bg antages opak) */
private fun compositeOver(fg: Color, bg: Color): Color {
    val aF = fg.alpha
    val r = fg.red   * aF + bg.red   * (1f - aF)
    val g = fg.green * aF + bg.green * (1f - aF)
    val b = fg.blue  * aF + bg.blue  * (1f - aF)
    return Color(r, g, b, 1f)
}

@Composable
fun NextGamesTheme(content: @Composable () -> Unit) {
    val ctx = LocalContext.current

    val pair by ColorPrefs.colors(ctx)
        .collectAsState(initial = ColorPrefs.DefaultBg to ColorPrefs.DefaultBtn)
    val container by ColorPrefs.container(ctx)
        .collectAsState(initial = ColorPrefs.DefaultContainer)
    val opacity by ColorPrefs.opacity(ctx)
        .collectAsState(initial = ColorPrefs.DefaultOpacity)
    val radiusDp by ColorPrefs.radius(ctx)
        .collectAsState(initial = ColorPrefs.DefaultRadiusDp)

    val bgColor  = pair.first
    val btnColor = pair.second

    val onBg  = bgColor.contrast()
    val onBtn = btnColor.contrast()

    val containerWithAlpha = container.copy(alpha = opacity.coerceIn(0f, 1f))
    val visibleContainer   = compositeOver(containerWithAlpha, bgColor)
    val onContainer        = visibleContainer.contrast()

    val scheme = lightColorScheme(
        background       = Color.Transparent,
        onBackground     = onBg,
        surface          = bgColor,
        onSurface        = onBg,
        surfaceVariant   = containerWithAlpha, // alle “kasser”
        onSurfaceVariant = onContainer,
        primary          = btnColor,
        onPrimary        = onBtn
    )

    // ► NY: Shapes styres globalt af slider (samme radius overalt)
    val shapes = Shapes(
        extraSmall = RoundedCornerShape(radiusDp.dp),
        small      = RoundedCornerShape(radiusDp.dp),
        medium     = RoundedCornerShape(radiusDp.dp),
        large      = RoundedCornerShape(radiusDp.dp),
        extraLarge = RoundedCornerShape(radiusDp.dp)
    )

    MaterialTheme(
        colorScheme = scheme,
        typography  = AppTypography,
        shapes      = shapes,
        content     = content
    )
}
