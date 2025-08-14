// ui/pages/PageScaffold.kt
package dk.nextgames.app.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageScaffold(
    title:   String,
    onBack:  () -> Unit,
    content: @Composable (PaddingValues) -> Unit = { pv ->            // ← default-indhold
        Box(
            modifier         = Modifier
                .fillMaxSize()
                .padding(pv),
            contentAlignment = Alignment.Center
        ) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
        }
    }
) {
    Scaffold(
        containerColor = Color.Transparent,                       // baggrund = gennemsigtig
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {                 // ← onBack stadig her
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent            // også gennemsigtig
                )
            )
        },
        content = content
    )
}
