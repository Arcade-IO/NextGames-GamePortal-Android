package dk.nextgames.app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class GameInterfaceViewModel : ViewModel() {

    /* ------------------------------------------------------------------ */
    /*  1) Firebase-bruger & database-reference                           */
    /* ------------------------------------------------------------------ */
    private val user  = Firebase.auth.currentUser
    private val dbRef = Firebase.database.reference

    /**  Bliver læst af AndroidBridge.getUserJson() i JavaScript  */
    @Volatile
    var userJson: String = buildJson(displayNameFallback = user?.displayName)
        private set

    init {
        loadNameFromDatabase()   // updatér userJson når navnet er hentet
    }

    /* ------------------------------------------------------------------ */
    /*  Henter `displayName` fra /users/{uid}                             */
    /* ------------------------------------------------------------------ */
    private fun loadNameFromDatabase() = viewModelScope.launch(Dispatchers.IO) {
        val u = user ?: return@launch
        val snap = dbRef.child("users").child(u.uid).get().await()
        val dbName = snap.child("displayName").getValue(String::class.java)

        if (!dbName.isNullOrBlank()) {
            userJson = buildJson(displayNameFallback = dbName)
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Bygger JSON-streng                                                */
    /* ------------------------------------------------------------------ */
    private fun buildJson(displayNameFallback: String?): String =
        JSONObject().apply {
            put("uid",   user?.uid ?: "")
            val name = when {
                !displayNameFallback.isNullOrBlank() -> displayNameFallback
                !user?.email.isNullOrBlank()         -> user.email!!.substringBefore('@')
                else                                 -> ""
            }
            put("name",  name)
            put("email", user?.email ?: "")
        }.toString()

    /* ------------------------------------------------------------------ */
    /*  2)  High-score upload                                             */
    /* ------------------------------------------------------------------ */
    fun submitHighScore(
        score:     Int,
        gameId:    String,
        gameTitle: String
    ) = viewModelScope.launch(Dispatchers.IO) {

        val u = user ?: return@launch

        val data = mapOf(
            "uid"         to u.uid,
            "displayName" to (u.displayName ?: ""),
            "email"       to (u.email ?: ""),
            "games_Id"    to gameId,
            "gameTitle"   to gameTitle,
            "score"       to score,
            "timestamp"   to ServerValue.TIMESTAMP
        )

        dbRef.child("highscores").push().setValue(data)
    }
}
