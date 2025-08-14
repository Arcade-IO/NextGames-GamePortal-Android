// LeaderboardViewModel.kt
package dk.nextgames.app.ui.ViewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.nextgames.app.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val games: List<GameStub>        = emptyList(),
    val selectedGameId: String       = "",
    val highscores: List<HighscoreEntry> = emptyList(),
    val loading: Boolean             = true,
    val error: String?               = null
)

class LeaderboardViewModel(
    private val repo: LeaderboardRepository = LeaderboardRepository()
) : ViewModel() {

    var uiState by mutableStateOf(LeaderboardUiState())
        private set

    init { loadGames() }

    /** 1) hent alle spil */
    private fun loadGames() = viewModelScope.launch {
        uiState = try {
            val g = repo.fetchGames()
            LeaderboardUiState(
                games = g,
                loading = false
            )
        } catch (e: Exception) {
            LeaderboardUiState(error = e.message, loading = false)
        }
    }

    /** kaldes fra UI når dropdown ændres */
    fun onSelectGame(id: String) {
        uiState = uiState.copy(selectedGameId = id, highscores = emptyList())
        observeHighscores(id)
    }

    private fun observeHighscores(gameId: String) = viewModelScope.launch {
        repo.highscoresFlow(gameId)
            .collect { list ->
                uiState = uiState.copy(highscores = list)
            }
    }
}
