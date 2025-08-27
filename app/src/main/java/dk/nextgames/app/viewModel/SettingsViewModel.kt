// app/src/main/java/dk/nextgames/app/viewModel/SettingsViewModel.kt
package dk.nextgames.app.viewModel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dk.nextgames.app.data.ColorPrefs
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Firebase KTX
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database

/* ---------- UI-state ---------- */
data class SettingsUiState(
    val bgColor:          Color = ColorPrefs.DefaultBg,
    val btnColor:         Color = ColorPrefs.DefaultBtn,
    val containerColor:   Color = ColorPrefs.DefaultContainer,
    val containerOpacity: Float = ColorPrefs.DefaultOpacity,  // 0f..1f
    val containerRadiusDp:Float = ColorPrefs.DefaultRadiusDp, // i dp (float)
    val loading:          Boolean = true
)

/* Util: Color → "#RRGGBB" (uden alpha) */
private fun Color.toHexRGB(): String {
    val rgb = this.toArgb() and 0x00FFFFFF
    return String.format("#%06X", rgb)
}

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val ctx = app.applicationContext

    private val _ui = MutableStateFlow(SettingsUiState())
    val ui: StateFlow<SettingsUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                ColorPrefs.colors(ctx),      // Pair(bg, btn)
                ColorPrefs.container(ctx),   // Color
                ColorPrefs.opacity(ctx),     // Float
                ColorPrefs.radius(ctx)       // Float dp
            ) { pair, cont, opac, radius ->
                SettingsUiState(
                    bgColor           = pair.first,
                    btnColor          = pair.second,
                    containerColor    = cont,
                    containerOpacity  = opac,
                    containerRadiusDp = radius,
                    loading           = false
                )
            }.collect { _ui.value = it }
        }
    }

    /* Gem + sync til Firebase /users/{uid}/theme_android */
    fun setBgColor(c: Color) = viewModelScope.launch {
        ColorPrefs.saveBg(ctx, c)
        val s = _ui.value
        saveToFirebase(
            bg = c, btn = s.btnColor,
            container = s.containerColor,
            opacity = s.containerOpacity,
            radiusDp = s.containerRadiusDp
        )
    }

    fun setBtnColor(c: Color) = viewModelScope.launch {
        ColorPrefs.saveBtn(ctx, c)
        val s = _ui.value
        saveToFirebase(
            bg = s.bgColor, btn = c,
            container = s.containerColor,
            opacity = s.containerOpacity,
            radiusDp = s.containerRadiusDp
        )
    }

    fun setContainerColor(c: Color) = viewModelScope.launch {
        ColorPrefs.saveContainer(ctx, c)
        val s = _ui.value
        saveToFirebase(
            bg = s.bgColor, btn = s.btnColor,
            container = c,
            opacity = s.containerOpacity,
            radiusDp = s.containerRadiusDp
        )
    }

    fun setContainerOpacity(value: Float) = viewModelScope.launch {
        val v = value.coerceIn(0f, 1f)
        ColorPrefs.saveOpacity(ctx, v)
        val s = _ui.value
        saveToFirebase(
            bg = s.bgColor, btn = s.btnColor,
            container = s.containerColor,
            opacity = v,
            radiusDp = s.containerRadiusDp
        )
    }

    /* ► NY: setContainerRadius (dp) */
    fun setContainerRadius(dp: Float) = viewModelScope.launch {
        val r = dp.coerceIn(0f, 64f)
        ColorPrefs.saveRadius(ctx, r)
        val s = _ui.value
        saveToFirebase(
            bg = s.bgColor, btn = s.btnColor,
            container = s.containerColor,
            opacity = s.containerOpacity,
            radiusDp = r
        )
    }

    private fun saveToFirebase(
        bg: Color,
        btn: Color,
        container: Color,
        opacity: Float,
        radiusDp: Float
    ) = viewModelScope.launch {
        val uid = Firebase.auth.currentUser?.uid ?: return@launch
        val ref = Firebase.database.reference
            .child("users").child(uid).child("theme_android")

        val payload = mapOf(
            "backgroundColor"  to bg.toHexRGB(),
            "buttonColor"      to btn.toHexRGB(),
            "containerColor"   to container.toHexRGB(),
            "containerOpacity" to opacity,
            "containerRadius"  to radiusDp
        )
        ref.setValue(payload).addOnFailureListener { it.printStackTrace() }
    }
}
