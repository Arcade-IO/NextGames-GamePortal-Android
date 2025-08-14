// LeaderboardRepository.kt
package dk.nextgames.app.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.nextgames.app.helper.ValueEventAdapter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class LeaderboardRepository {

    private val db = Firebase.database.reference

    /** Alle spil -> dropdown */
    suspend fun fetchGames(): List<GameStub> {
        val snap = db.child("games").get().await()
        if (!snap.exists()) return emptyList()
        return snap.children.map {
            GameStub(
                id   = it.key ?: "",
                name = it.child("title").getValue(String::class.java) ?: ""
            )
        }
    }

    /** Flow der emmitter highscores for ét spil i realtime */
    fun highscoresFlow(gameId: String): Flow<List<HighscoreEntry>> = callbackFlow {
        val ref = db.child("highscores")
        val listener = ref.addValueEventListener(ValueEventAdapter { snap ->
            trySend(parseHighscores(snap, gameId))
        })
        awaitClose { ref.removeEventListener(listener) }
    }.conflate()

    private fun parseHighscores(
        snap: DataSnapshot,
        gameId: String
    ): List<HighscoreEntry> {
        if (!snap.exists()) return emptyList()
        return snap.children
            .mapNotNull { child ->
                if (child.child("games_Id").getValue(String::class.java) != gameId) return@mapNotNull null
                HighscoreEntry(
                    displayName = child.child("displayName").getValue(String::class.java) ?: "",
                    gameTitle   = child.child("gameTitle").getValue(String::class.java) ?: "",
                    score       = child.child("score").getValue(Int::class.java) ?: 0
                )
            }
            .sortedByDescending { it.score }
    }
}
