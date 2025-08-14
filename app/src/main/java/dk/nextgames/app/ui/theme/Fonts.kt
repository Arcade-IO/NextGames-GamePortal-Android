package dk.nextgames.app.ui.theme

import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp
import dk.nextgames.app.R   // TTF-filerne i res/font/

// ❶  FontFamily med dine TTF’er
val Inconsolata = FontFamily(
    Font(R.font.inconsolata_regular, weight = FontWeight.Normal),
)

// ❷  Ét fælles Typography-objekt der bruger fonten
val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = Inconsolata, fontWeight = FontWeight.Bold,   fontSize = 50.sp),
    titleLarge   = TextStyle(fontFamily = Inconsolata, fontWeight = FontWeight.Bold,   fontSize = 26.sp),
    bodyLarge    = TextStyle(fontFamily = Inconsolata, fontWeight = FontWeight.Bold, fontSize = 20.sp),
    labelLarge   = TextStyle(fontFamily = Inconsolata, fontWeight = FontWeight.Bold,   fontSize = 18.sp)
    // tilføj flere varianter efter behov
)
