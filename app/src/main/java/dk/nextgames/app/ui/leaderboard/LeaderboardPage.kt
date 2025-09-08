package dk.nextgames.app.ui.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dk.nextgames.app.data.ColorPrefs
import dk.nextgames.app.data.GameStub
import dk.nextgames.app.data.HighscoreEntry
import dk.nextgames.app.ui.ViewModel.LeaderboardViewModel
import dk.nextgames.app.ui.pages.PageScaffold

/* ====== TABEL-MÅL (bruges i både header og rækker) ====== */
private val TABLE_HPAD: Dp = 6.dp            // indre venstre/højre padding
private val COL_POS: Dp = 56.dp              // bredere POS: plads til tal + pokal
private val COL_SCORE: Dp = 80.dp            // fast bredde til score
private val GAP_SCORE_TO_NAME: Dp = 8.dp     // ens afstand mellem SCORE og NAME

/* ====== Label-indryk for at flugte med TextField-indhold ====== */
private val FIELD_LABEL_INDENT: Dp = 16.dp   // ca. samme som M3 TextField indholdspadding

/* ====== Pokalfarver ====== */
private val GOLD   = Color(0xFFFFD700)      // #FFD700
private val SILVER = Color(0xFFC0C0C0)      // #C0C0C0
private val BRONZE = Color(0xFFCD7F32)      // #CD7F32

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardPage(
    onBack: () -> Unit,
    vm: LeaderboardViewModel = viewModel()
) {
    val state = vm.uiState
    val ctx = LocalContext.current

    // Base-farver (bg/btn) fra DataStore
    val colorPair by ColorPrefs.colors(ctx)
        .collectAsState(initial = ColorPrefs.DefaultBg to ColorPrefs.DefaultBtn)
    val userBg = colorPair.first
    val onBg  = if (userBg.luminance() > 0.5f) Color.Black else Color.White

    // Container-look fra Settings
    val containerColor by ColorPrefs.container(ctx)
        .collectAsState(initial = ColorPrefs.DefaultContainer)
    val containerOpacity by ColorPrefs.opacity(ctx)
        .collectAsState(initial = ColorPrefs.DefaultOpacity)
    val containerRadiusDp by ColorPrefs.radius(ctx)
        .collectAsState(initial = ColorPrefs.DefaultRadiusDp)

    val panelShape = RoundedCornerShape(containerRadiusDp.dp)
    val panelColor = containerColor.copy(alpha = containerOpacity)

    // ► MENUBAGGRUND = opak version af container (for at undgå hvide kanter i Popup)
    val menuBgColor = compositeOver(panelColor, userBg)
    val menuOnColor = if (menuBgColor.luminance() > 0.5f) Color.Black else Color.White

    PageScaffold("Leaderboard", onBack) { inner ->
        if (state.loading) {
            Box(
                Modifier.padding(inner).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@PageScaffold
        }

        Column(
            Modifier.padding(inner).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = panelShape,
                color = panelColor,
                contentColor = onBg,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                border = BorderStroke(1.dp, onBg.copy(alpha = 0.10f))
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ► Indryk label så den flugter med TextField’ets indre start
                    Text(
                        text = "Selected Game",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = onBg,
                        modifier = Modifier.padding(start = FIELD_LABEL_INDENT)
                    )

                    // Felt = kassefarve, MENU = container (opak)
                    GameDropdown(
                        games        = state.games,
                        selected     = state.selectedGameId,
                        onSelect     = vm::onSelectGame,
                        fieldBgColor = panelColor,    // TextField (må gerne være semi-trans)
                        fieldOnColor = onBg,
                        menuBgColor  = menuBgColor,   // dropdownmenu = container (OPAK)
                        menuOnColor  = menuOnColor,
                        modifier     = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    if (state.highscores.isEmpty()) {
                        Text(
                            "Der er endnu ikke highscores.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = onBg
                        )
                    } else {
                        TableHeader(onBg = onBg)

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            itemsIndexed(state.highscores) { index, entry ->
                                val rowBg =
                                    if (index % 2 == 0)
                                        containerColor.copy(
                                            alpha = (containerOpacity + 0.06f).coerceIn(0f, 1f)
                                        )
                                    else Color.Transparent

                                LeaderboardRow(
                                    index = index,
                                    e = entry,
                                    textColor = onBg,
                                    rowBg = rowBg,
                                    rowShape = RoundedCornerShape((containerRadiusDp / 2f).dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------- UI: Header + Row ---------- */

@Composable
private fun TableHeader(onBg: Color) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = TABLE_HPAD),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.width(COL_POS)) { HeaderCell("POS", onBg) }
            Box(Modifier.width(COL_SCORE)) { HeaderCell("SCORE", onBg) }
            Spacer(Modifier.width(GAP_SCORE_TO_NAME))
            Box(Modifier.weight(1f)) { HeaderCell("NAME", onBg) }
            Box(Modifier.weight(1f)) { HeaderCell("GAME", onBg) }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp, bottom = 6.dp),
            thickness = 1.dp,
            color = onBg.copy(alpha = 0.55f)
        )
    }
}

@Composable
private fun LeaderboardRow(
    index: Int,
    e: HighscoreEntry,
    textColor: Color,
    rowBg: Color,
    rowShape: RoundedCornerShape
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .background(rowBg)
            .padding(horizontal = TABLE_HPAD, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // POS-kolonne (fast bredde) — tal + pokal står SAMMEN, venstrestillet
        Box(Modifier.width(COL_POS)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "${index + 1}",
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Start
                )
                if (index in 0..2) {
                    val trophyTint = when (index) {
                        0 -> GOLD
                        1 -> SILVER
                        else -> BRONZE
                    }
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = trophyTint,
                        modifier = Modifier.width(16.dp)
                    )
                }
            }
        }

        // SCORE-kolonne (fast bredde, venstrejusteret som headeren)
        Box(
            Modifier.width(COL_SCORE),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "${e.score}",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.width(GAP_SCORE_TO_NAME))

        // NAME-kolonne
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            Text(
                e.displayName,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }

        // GAME-kolonne
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            Text(
                e.gameTitle,
                color = textColor.copy(alpha = 0.90f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}

/* ---------- Dropdown (samme tema, forankret korrekt) ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDropdown(
    games: List<GameStub>,
    selected: String,
    onSelect: (String) -> Unit,
    fieldBgColor: Color,  // TextField container (kan være semi-trans)
    fieldOnColor: Color,
    menuBgColor: Color,   // MENUBAGGRUND = container (OPAK)
    menuOnColor: Color,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = games.firstOrNull { it.id == selected }?.name ?: "Vælg et spil"
    val shape = RoundedCornerShape(12.dp)

    Surface(
        color = fieldBgColor,
        contentColor = fieldOnColor,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        shape = shape,
        border = BorderStroke(1.dp, fieldOnColor.copy(alpha = 0.12f)),
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
                label = { Text("Games", color = fieldOnColor.copy(alpha = 0.9f)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .clickable { expanded = !expanded }
                    .menuAnchor(), // anker til menuen
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = fieldBgColor,
                    unfocusedContainerColor = fieldBgColor,
                    disabledContainerColor = fieldBgColor,
                    errorContainerColor = fieldBgColor,
                    focusedTextColor = fieldOnColor,
                    unfocusedTextColor = fieldOnColor,
                    disabledTextColor = fieldOnColor.copy(alpha = 0.38f),
                    errorTextColor = MaterialTheme.colorScheme.error,
                    cursorColor = fieldOnColor,
                    errorCursorColor = fieldOnColor,
                    focusedIndicatorColor = fieldOnColor.copy(alpha = 0.55f),
                    unfocusedIndicatorColor = fieldOnColor.copy(alpha = 0.35f),
                    disabledIndicatorColor = fieldOnColor.copy(alpha = 0.20f),
                    errorIndicatorColor = MaterialTheme.colorScheme.error
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(0.dp, 6.dp),
                shape = shape,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                modifier = Modifier.background(menuBgColor, shape)
            ) {
                games.forEach { g ->
                    DropdownMenuItem(
                        text = { Text(g.name, color = menuOnColor) },
                        onClick = {
                            expanded = false
                            onSelect(g.id)
                        },
                        colors = MenuDefaults.itemColors(textColor = menuOnColor)
                    )
                }
            }
        }
    }
}

/* ---------- Helpers ---------- */

@Composable
private fun HeaderCell(text: String, color: Color) {
    Text(
        text = text,
        color = color.copy(alpha = 0.85f),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Start
    )
}

/* Gør en farve OPAK over en baggrund (samme idé som i dit theme) */
private fun compositeOver(fg: Color, bg: Color): Color {
    val a = fg.alpha
    val r = fg.red * a + bg.red * (1f - a)
    val g = fg.green * a + bg.green * (1f - a)
    val b = fg.blue * a + bg.blue * (1f - a)
    return Color(r, g, b, 1f)
}
