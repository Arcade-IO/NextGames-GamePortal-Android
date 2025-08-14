package dk.nextgames.app.ui.games

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class GamesUiState(
    val games:   List<Game> = emptyList(),
    val loading: Boolean      = true,
    val error:   String?      = null
)

class GamesViewModel(
    private val repo: GamesRepository = GamesRepository()
) : ViewModel() {

    var uiState by mutableStateOf(GamesUiState())
        private set

    init { loadGames() }

    private fun loadGames() = viewModelScope.launch {
        uiState = try {
            val list = repo.fetchAndroidGames()
            GamesUiState(games = list, loading = false)
        } catch (e: Exception) {
            GamesUiState(error = e.message, loading = false)
        }
    }
}
