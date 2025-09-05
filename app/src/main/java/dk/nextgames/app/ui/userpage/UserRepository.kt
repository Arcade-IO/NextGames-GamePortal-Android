package dk.nextgames.app.ui.userpage

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.nextgames.app.data.UserProfile
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = Firebase.auth
    private val db = Firebase.database.reference

    suspend fun getCurrentUserProfile(): UserProfile? {
        val user = auth.currentUser ?: return null
        val uid = user.uid

        val snap = db.child("users").child(uid).get().await()
        if (snap.exists()) return snap.toProfile(uid)

        return UserProfile(
            uid = uid,
            name = user.displayName ?: "",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString()
        )
    }

    private fun DataSnapshot.toProfile(uid: String): UserProfile {
        val name = child("displayName").getValue(String::class.java) ?: ""
        val email = child("email").getValue(String::class.java) ?: ""
        val photoUrl = child("photoURL").getValue(String::class.java) ?: "" // Firebase'de key büyük URL

        android.util.Log.d("UserRepository", "photoURL for $uid = $photoUrl")

        return UserProfile(
            uid = uid,
            name = name,
            email = email,
            photoUrl = photoUrl
        )
    }
}
