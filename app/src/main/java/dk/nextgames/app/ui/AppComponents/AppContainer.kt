// app/src/main/java/dk/nextgames/app/ui/common/AppContainers.kt
package dk.nextgames.app.ui.common

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Fælles “kasse”-kort:
 * - Farve: MaterialTheme.colorScheme.surfaceVariant (din containerColor)
 * - Tekst: MaterialTheme.colorScheme.onSurfaceVariant
 * - Form:  MaterialTheme.shapes.medium
 *
 * Brug denne overalt i stedet for at sætte farver manuelt.
 */
@Composable
fun ContainerCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor   = MaterialTheme.colorScheme.onSurfaceVariant
    )
    if (onClick != null) {
        Card(
            onClick  = onClick,
            modifier = modifier,
            shape    = MaterialTheme.shapes.medium,
            colors   = colors
        ) { content() }
    } else {
        Card(
            modifier = modifier,
            shape    = MaterialTheme.shapes.medium,
            colors   = colors
        ) { content() }
    }
}

/**
 * Fælles “kasse”-surface (hvis du ikke vil have Card elevation/border ripple).
 * Bruges til sektioner, bokse osv. uden klik.
 */
@Composable
fun ContainerSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier      = modifier,
        color         = MaterialTheme.colorScheme.surfaceVariant,
        contentColor  = MaterialTheme.colorScheme.onSurfaceVariant,
        shape         = MaterialTheme.shapes.medium,
        content       = content
    )
}
