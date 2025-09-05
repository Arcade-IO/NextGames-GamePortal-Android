package dk.nextgames.app.data

import androidx.compose.ui.graphics.Color

/**
 * ChatTheme holds colors for chat popup and bubbles.
 */
data class ChatTheme(
    val name: String,
    val backgroundColor: Color,
    val textColor: Color,
    val bubbleColorOwn: Color,
    val bubbleColorOther: Color,
    val bubbleTextColorOwn: Color,
    val bubbleTextColorOther: Color
)

/**
 * List of available chat themes.
 */
val chatThemes = listOf(
    ChatTheme(
        name = "Light",
        backgroundColor = Color.White,
        textColor = Color.Black,
        bubbleColorOwn = Color(0xFF1976D2),
        bubbleColorOther = Color(0xFFE3E3E3),
        bubbleTextColorOwn = Color.White,
        bubbleTextColorOther = Color.Black
    ),
    ChatTheme(
        name = "Dark",
        backgroundColor = Color(0xFF181818),
        textColor = Color(0xFFD8D8D8),
        bubbleColorOwn = Color(0xFF1976D2),
        bubbleColorOther = Color(0xFF333333),
        bubbleTextColorOwn = Color.White,
        bubbleTextColorOther = Color.White
    ),
    ChatTheme(
        name = "Green",
        backgroundColor = Color(0xFFE8F5E9),
        textColor = Color(0xFF1B5E20),
        bubbleColorOwn = Color(0xFF43A047),
        bubbleColorOther = Color(0xFFC8E6C9),
        bubbleTextColorOwn = Color.White,
        bubbleTextColorOther = Color(0xFF1B5E20)
    ),
    ChatTheme(
        name = "Red",
        backgroundColor = Color(0xFFFFEBEE),
        textColor = Color(0xFFD32F2F),
        bubbleColorOwn = Color(0xFFD32F2F),
        bubbleColorOther = Color(0xFFFFCDD2),
        bubbleTextColorOwn = Color.White,
        bubbleTextColorOther = Color(0xFFD32F2F)
    ),
    ChatTheme(
        name = "Purple",
        backgroundColor = Color(0xFFF3E5F5),
        textColor = Color(0xFF6A1B9A),
        bubbleColorOwn = Color(0xFF8E24AA),
        bubbleColorOther = Color(0xFFE1BEE7),
        bubbleTextColorOwn = Color.White,
        bubbleTextColorOther = Color(0xFF6A1B9A)
    ),
    ChatTheme(
        name = "Dark Purple",
        backgroundColor = Color(0xFF181818),
        textColor = Color(0xFF6A1B9A),
        bubbleColorOwn = Color(0xFF8E24AA),
        bubbleColorOther = Color(0xFFE1BEE7),
        bubbleTextColorOwn = Color.White,
        bubbleTextColorOther = Color(0xFF6A1B9A)
    )
)

