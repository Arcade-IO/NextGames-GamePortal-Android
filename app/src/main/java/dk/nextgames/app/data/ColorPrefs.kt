/* ------------- ColorPrefs.kt ---------------- */
package dk.nextgames.app.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb          // <-- IMPORT
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val STORE_NAME = "ui_prefs"
val Context.dataStore by preferencesDataStore(STORE_NAME)

object ColorPrefs {

    private val BG_KEY  = intPreferencesKey("bg_color")
    private val BTN_KEY = intPreferencesKey("btn_color")

    val DefaultBg  = Color(0xFFFFFFFF)
    val DefaultBtn = Color(0xFF2B7E76)

    /* ► Hent farver */
    fun colors(ctx: Context) =
        ctx.dataStore.data
            .catch { e ->
                if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
                else throw e
            }
            .map { pref ->
                val bg  = pref[BG_KEY]  ?: DefaultBg.toArgb()
                val btn = pref[BTN_KEY] ?: DefaultBtn.toArgb()
                Color(bg) to Color(btn)               // ARGB → Color
            }

    /* ► Gem farver  (brug toArgb!) */
    suspend fun saveBg(ctx: Context, c: Color) =
        ctx.dataStore.edit { it[BG_KEY]  = c.toArgb() }

    suspend fun saveBtn(ctx: Context, c: Color) =
        ctx.dataStore.edit { it[BTN_KEY] = c.toArgb() }
}
