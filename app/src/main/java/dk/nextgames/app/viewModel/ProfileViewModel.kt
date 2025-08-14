package dk.nextgames.app.viewModel   // ← matcher dit import

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.nextgames.app.data.UserProfile
import kotlinx.coroutines.launch
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/* ---------- simple repo direkte i ViewModel (kan flyttes ud senere) ---------- */
private suspend fun fetchProfile(): UserProfile? {
    val auth = Firebase.auth
    val db   = Firebase.database.reference
    val user = auth.currentUser ?: return null
    val uid  = user.uid

    val snap = db.child("users").child(uid).get().await()
    return if (snap.exists()) {
        UserProfile(
            uid   = uid,
            name  = snap.child("displayName").getValue(String::class.java) ?: "",
            email = snap.child("email").getValue(String::class.java) ?: ""
        )
    } else {
        UserProfile(uid, user.displayName ?: "", user.email ?: "")
    }
}

/* ---------- UI-state ---------- */
data class ProfileUiState(
    val profile: UserProfile? = null,
    val loading: Boolean      = true,
    val error:   String?      = null
)

/* ---------- ViewModel ---------- */
class ProfileViewModel : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init { load() }

    private fun load() = viewModelScope.launch {
        uiState = try {
            val p = fetchProfile()
            if (p == null) ProfileUiState(error = "Ingen bruger logget ind", loading = false)
            else           ProfileUiState(profile = p, loading = false)
        } catch (e: Exception) {
            ProfileUiState(error = e.message, loading = false)
        }
    }
}
