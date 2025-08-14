package dk.nextgames.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

class LoginViewModel : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    private val auth = Firebase.auth
    private val db   = Firebase.database

    /** Opdater tekstfelter */
    fun onEmailChange(v: String)    { _ui.value = _ui.value.copy(email    = v) }
    fun onPasswordChange(v: String) { _ui.value = _ui.value.copy(password = v) }

    /** Tryk på “Log ind” */
    fun login(onSuccess: () -> Unit) = viewModelScope.launch {
        val (email, pass) = _ui.value
        if (email.isBlank() || pass.isBlank()) {
            _ui.value = _ui.value.copy(error = "Email og password skal udfyldes")
            return@launch
        }
        _ui.value = _ui.value.copy(isLoading = true, error = null)

        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { cred ->
                if (!cred.user!!.isEmailVerified) {
                    auth.signOut()
                    _ui.value = _ui.value.copy(isLoading = false,
                        error = "Verificér email før login")
                    return@addOnSuccessListener
                }
                // Hent ekstra bruger-metadata fra Realtime DB (displayName, isAdmin)
                db.reference.child("users").child(cred.user!!.uid).get()
                    .addOnSuccessListener { snap ->
                        snap.child("displayName").getValue(String::class.java)?.let { dn ->
                            // Gem lokalt fx i SharedPreferences el. DataStore
                        }
                        _ui.value = _ui.value.copy(isLoading = false)
                        onSuccess()
                    }
            }
            .addOnFailureListener { e ->
                _ui.value = _ui.value.copy(isLoading = false, error = e.message)
            }
    }

    fun onNameChange(s: String) {

    }
}
