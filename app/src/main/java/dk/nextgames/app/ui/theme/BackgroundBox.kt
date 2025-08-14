package dk.nextgames.app.ui.theme

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.palette.graphics.Palette
import dk.nextgames.app.R
import dk.nextgames.app.data.ColorPrefs             // ❶ brugerfarver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/* Eksponeres så temaet ved om baggrunden er mørk */
val LocalIsDarkBackground = compositionLocalOf { true }

@Composable
fun BackgroundBox(
    resId:   Int = R.drawable.bg_retro,
    modifier: Modifier = Modifier,
    content:  @Composable BoxScope.() -> Unit
) {
    /* ---------- 1) Læs farve valgt af brugeren ---------- */
    val ctx = LocalContext.current
    val userColors by ColorPrefs.colors(ctx)
        .collectAsState(ColorPrefs.DefaultBg to ColorPrefs.DefaultBtn)
    val userBg = userColors.first        // kun baggrundsfarven

    /* ---------- 2) Bestem om kombinationen er “mørk” ---------- */
    val isDark = remember(resId, userBg) { mutableStateOf(true) }

    LaunchedEffect(resId, userBg) {
        isDark.value = withContext(Dispatchers.Default) {
            /* Dominant farve fra PNG’en */
            val bmp = BitmapFactory.decodeResource(ctx.resources, resId)
            val dom  = Palette.from(bmp).generate().getDominantColor(0xFFFFFF)
            val r = (dom shr 16) and 0xFF
            val g = (dom shr  8) and 0xFF
            val b =  dom         and 0xFF
            val domLum = (r * 299 + g * 587 + b * 114) / 1000       // 0-255
            /* Gennemsnit af billedets + brugerens luminans */
            val avgLum = ((domLum / 255f) + userBg.luminance()) / 2f
            avgLum < .5f            // < 0.5 ⇒ mørk baggrund
        }
    }

    /* ---------- 3) Tegn farve → PNG → UI ---------- */
    CompositionLocalProvider(LocalIsDarkBackground provides isDark.value) {
        Box(modifier.fillMaxSize()) {

            /* brugerens rene farve – vises i de transparente områder */
            Box(
                Modifier
                    .fillMaxSize()
                    .background(userBg)
            )

            /* dit retro-PNG med transparente striber */
            Image(
                painter = painterResource(resId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            /* resten af app-UI’et */
            content()
        }
    }
}
