package dk.nextgames.app.ui.games

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class GamesRepository {
    private val db = Firebase.database.reference

    /** Henter kun spil hvor platform == "Android" (case-insensitive) */
    suspend fun fetchAndroidGames(): List<Game> {
        val snap = db.child("games").get().await()
        if (!snap.exists()) return emptyList()

        return snap.children
            .mapNotNull { it.toGame() }
            .filter { it.platform.equals("android", ignoreCase = true) }
    }

    private fun DataSnapshot.toGame(): Game? =
        getValue(Game::class.java)?.copy(id = key ?: "")
}
