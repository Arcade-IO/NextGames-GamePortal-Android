package dk.nextgames.app.ui.games

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesPage(
    onBack: () -> Unit,
    onGameClicked: (Game) -> Unit,            // ①  Én parameter!
    vm: GamesViewModel = viewModel()
) {
    val state = vm.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Games") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { pad ->
        when {
            state.loading -> Center(pad) { CircularProgressIndicator() }
            state.games.isEmpty() -> Center(pad) { Text("There is no games to show") }

            else -> LazyColumn(
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.games) { game ->
                    GameCard(game) { onGameClicked(game) }   // ②
                }
            }
        }
    }
}

@Composable
private fun GameCard(game: Game, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = game.imageUrl,
                contentDescription = game.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Spacer(Modifier.width(16.dp))
            Text(game.title, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun Center(pad: PaddingValues, content: @Composable BoxScope.() -> Unit) =
    Box(
        modifier = Modifier
            .padding(pad)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
        content = content
    )
