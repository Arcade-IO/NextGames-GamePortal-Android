// app/src/main/java/dk/nextgames/app/data/ColorPrefs.kt
package dk.nextgames.app.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val STORE_NAME = "ui_prefs"
val Context.dataStore by preferencesDataStore(STORE_NAME)

/** (valgfrit) samlet holder hvis du vil bruge den senere */
data class ThemeColorsTriple(
    val bg: Color,
    val btn: Color,
    val container: Color
)

object ColorPrefs {

    private val BG_KEY         = intPreferencesKey("bg_color")
    private val BTN_KEY        = intPreferencesKey("btn_color")
    private val CONTAINER_KEY  = intPreferencesKey("container_color")
    private val OPACITY_KEY    = floatPreferencesKey("container_opacity")     // 0f..1f
    private val RADIUS_KEY     = floatPreferencesKey("container_radius_dp")   // i dp

    /** Standardværdier */
    val DefaultBg         = Color(0xFFFFFFFF)
    val DefaultBtn        = Color(0xFF2B7E76)
    val DefaultContainer  = Color(0xFFEAE7F2)
    val DefaultOpacity    = 0.10f        // 10% sort-agtig “skygge”
    val DefaultRadiusDp   = 12f          // 12.dp som standard runding

    /* ► Hent baggrund + knap som Pair */
    fun colors(ctx: Context) =
        ctx.dataStore.data
            .catch { e ->
                if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
                else throw e
            }
            .map { pref ->
                val bg  = pref[BG_KEY]  ?: DefaultBg.toArgb()
                val btn = pref[BTN_KEY] ?: DefaultBtn.toArgb()
                Color(bg) to Color(btn)
            }

    /* ► Hent container-farve */
    fun container(ctx: Context) =
        ctx.dataStore.data
            .catch { e ->
                if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
                else throw e
            }
            .map { pref ->
                val cont = pref[CONTAINER_KEY] ?: DefaultContainer.toArgb()
                Color(cont)
            }

    /* ► Hent container-opacity (0..1) */
    fun opacity(ctx: Context) =
        ctx.dataStore.data
            .catch { e ->
                if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
                else throw e
            }
            .map { pref ->
                (pref[OPACITY_KEY] ?: DefaultOpacity).coerceIn(0f, 1f)
            }

    /* ► Hent container-radius i dp (float) */
    fun radius(ctx: Context) =
        ctx.dataStore.data
            .catch { e ->
                if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
                else throw e
            }
            .map { pref ->
                (pref[RADIUS_KEY] ?: DefaultRadiusDp).coerceIn(0f, 64f)
            }

    /* ► Gem værdier */
    suspend fun saveBg(ctx: Context, c: Color)         =
        ctx.dataStore.edit { it[BG_KEY]        = c.toArgb() }

    suspend fun saveBtn(ctx: Context, c: Color)        =
        ctx.dataStore.edit { it[BTN_KEY]       = c.toArgb() }

    suspend fun saveContainer(ctx: Context, c: Color)  =
        ctx.dataStore.edit { it[CONTAINER_KEY] = c.toArgb() }

    suspend fun saveOpacity(ctx: Context, value: Float) =
        ctx.dataStore.edit { it[OPACITY_KEY]   = value.coerceIn(0f, 1f) }

    suspend fun saveRadius(ctx: Context, dp: Float)     =
        ctx.dataStore.edit { it[RADIUS_KEY]    = dp.coerceIn(0f, 64f) }
}
