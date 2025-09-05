package dk.nextgames.app.viewModel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.nextgames.app.data.UserProfile
import kotlinx.coroutines.launch
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

private suspend fun fetchProfile(): UserProfile? {
    val auth = Firebase.auth
    val db   = Firebase.database.reference
    val user = auth.currentUser ?: return null
    val uid  = user.uid

    val snap = db.child("users").child(uid).get().await()
    return if (snap.exists()) {
        UserProfile(
            uid      = uid,
            name     = snap.child("displayName").getValue(String::class.java) ?: "",
            email    = snap.child("email").getValue(String::class.java) ?: "",
            photoUrl = snap.child("photoURL").getValue(String::class.java) ?: ""
        )
    } else {
        UserProfile(
            uid      = uid,
            name     = user.displayName ?: "",
            email    = user.email ?: "",
            photoUrl = user.photoUrl?.toString()
        )
    }
}

data class ProfileUiState(
    val profile: UserProfile? = null,
    val loading: Boolean      = true,
    val error:   String?      = null
)

class ProfileViewModel : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        load()
    }

    fun load() = viewModelScope.launch {
        uiState = try {
            val p = fetchProfile()
            if (p == null) {
                ProfileUiState(error = "No user logged in", loading = false)
            } else {
                ProfileUiState(profile = p, loading = false)
            }
        } catch (e: Exception) {
            ProfileUiState(error = e.message ?: "Unknown error", loading = false)
        }
    }

    fun uploadImageToCloudinary(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)!!
                val bytes = inputStream.readBytes()
                inputStream.close()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        "profile.jpg",
                        bytes.toRequestBody("image/*".toMediaTypeOrNull())
                    )
                    .addFormDataPart("upload_preset", "ImageUploader")
                    .build()

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/dshaoiftz/image/upload")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()
                val secureUrl = JSONObject(body).getString("secure_url")

                val uid = Firebase.auth.currentUser?.uid ?: return@launch
                Firebase.database.reference
                    .child("users").child(uid).child("photoURL")
                    .setValue(secureUrl).await()

                withContext(Dispatchers.Main) {
                    uiState = uiState.copy(
                        profile = uiState.profile?.copy(photoUrl = secureUrl)
                    )
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiState = uiState.copy(error = e.message)
                }
            }
        }
    }
}
