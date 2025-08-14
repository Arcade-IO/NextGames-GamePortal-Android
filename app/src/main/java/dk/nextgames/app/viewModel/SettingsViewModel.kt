// app/src/main/java/dk/nextgames/app/viewModel/SettingsViewModel.kt
package dk.nextgames.app.viewModel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dk.nextgames.app.data.ColorPrefs
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/* ---------- UI-state ---------- */
data class SettingsUiState(
    val bgColor:  Color = ColorPrefs.DefaultBg,
    val btnColor: Color = ColorPrefs.DefaultBtn,
    val loading:  Boolean = true
)

/* ---------- ViewModel ---------- */
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val ctx = app.applicationContext

    private val _ui = MutableStateFlow(SettingsUiState())
    val ui: StateFlow<SettingsUiState> = _ui.asStateFlow()

    init {
        // ❶  Lyt til DataStore → opdatér UI hver gang noget ændres
        viewModelScope.launch {
            ColorPrefs.colors(ctx).collect { (bg, btn) ->
                _ui.value = SettingsUiState(bg, btn, loading = false)
            }
        }
    }

    /* ❷  Gem valgt baggrunds-/knap-farve  */
    fun setBgColor(c: Color)  = viewModelScope.launch { ColorPrefs.saveBg(ctx, c) }
    fun setBtnColor(c: Color) = viewModelScope.launch { ColorPrefs.saveBtn(ctx, c) }
}
