package dk.nextgames.app.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val message: String? = null
)

class ForgotPasswordViewModel : ViewModel() {

    private val _ui = MutableStateFlow(ForgotPasswordUiState())
    val ui: StateFlow<ForgotPasswordUiState> = _ui

    fun onEmailChange(newEmail: String) {
        _ui.value = _ui.value.copy(email = newEmail)
    }

    fun resetPassword() {
        val email = _ui.value.email
        if (email.isBlank()) {
            _ui.value = _ui.value.copy(message = "Email required")
            return
        }

        _ui.value = _ui.value.copy(isLoading = true, message = null)

        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _ui.value = _ui.value.copy(
                        isLoading = false,
                        message = "Check your email for reset link"
                    )
                } else {
                    _ui.value = _ui.value.copy(
                        isLoading = false,
                        message = task.exception?.localizedMessage ?: "Failed to send reset email"
                    )
                }
            }
    }
}
