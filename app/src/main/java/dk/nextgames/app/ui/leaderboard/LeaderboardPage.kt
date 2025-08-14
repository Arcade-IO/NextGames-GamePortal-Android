package dk.nextgames.app.ui.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.nextgames.app.data.ColorPrefs
import dk.nextgames.app.data.GameStub
import dk.nextgames.app.data.HighscoreEntry
import dk.nextgames.app.ui.ViewModel.LeaderboardViewModel
import dk.nextgames.app.ui.pages.PageScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardPage(
    onBack: () -> Unit,
    vm: LeaderboardViewModel = viewModel()
) {
    val state = vm.uiState

    // 1) Hent bg+btn fra DataStore som én Pair
    val ctx = LocalContext.current
    val colorPair by ColorPrefs
        .colors(ctx)
        .collectAsState(initial = ColorPrefs.DefaultBg to ColorPrefs.DefaultBtn)
    val userBg = colorPair.first

    // 2) Beregn kontrast (sort ↔︎ hvid)
    val onBg = if (userBg.luminance() > 0.5f) Color.Black else Color.White

    PageScaffold("Leaderboard", onBack) { inner ->
        if (state.loading) {
            Box(
                Modifier
                    .padding(inner)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@PageScaffold
        }

        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // dropdown uden kort:
            GameDropdown(
                games    = state.games,
                selected = state.selectedGameId,
                onSelect = vm::onSelectGame,
                bgColor  = userBg,
                onColor  = onBg,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            if (state.highscores.isEmpty()) {
                Text(
                    "Der er endnu ikke highscores.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = onBg
                )
            } else {
                LazyColumn(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HeaderCell("POS",   onBg)
                            HeaderCell("SCORE", onBg)
                            HeaderCell("NAME",  onBg)
                            HeaderCell("GAME",  onBg)
                        }
                        HorizontalDivider(Modifier, DividerDefaults.Thickness, color = onBg)
                    }
                    itemsIndexed(state.highscores) { idx, entry ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            BodyCell("${idx+1}",       onBg)
                            BodyCell("${entry.score}", onBg)
                            BodyCell(entry.displayName,onBg)
                            BodyCell(entry.gameTitle,  onBg)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDropdown(
    games: List<GameStub>,
    selected: String,
    onSelect: (String)->Unit,
    bgColor: Color,
    onColor: Color,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = games.firstOrNull { it.id == selected }?.name
        ?: "Vælg et spil"

    Surface(
        color = bgColor,
        contentColor = onColor,
        tonalElevation = 1.dp,
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Games") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),  // ← vigtig
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = bgColor,
                    unfocusedContainerColor = bgColor,
                    disabledContainerColor = bgColor,
                    errorContainerColor = bgColor,

                    focusedTextColor = onColor,
                    unfocusedTextColor = onColor,
                    disabledTextColor = onColor.copy(alpha = 0.38f),
                    errorTextColor = MaterialTheme.colorScheme.error,
                    cursorColor = onColor,
                    errorCursorColor = onColor,

                    focusedIndicatorColor = onColor,
                    unfocusedIndicatorColor = onColor.copy(alpha = 0.6f),
                    disabledIndicatorColor = onColor.copy(alpha = 0.38f),
                    errorIndicatorColor = MaterialTheme.colorScheme.error
                )
            )


            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(bgColor),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                games.forEach { g ->
                    DropdownMenuItem(
                        text = { Text(g.name, color = onColor) },
                        onClick = {
                            expanded = false
                            onSelect(g.id)
                        },
                        modifier = Modifier.background(bgColor)
                    )
                }
            }

        }
    }
}




@Composable
private fun HeaderCell(text: String, color: Color) =
    Text(text, color=color, style = MaterialTheme.typography.labelLarge)

@Composable
private fun BodyCell(text: String, color: Color) =
    Text(text, color=color, style = MaterialTheme.typography.bodyMedium)
