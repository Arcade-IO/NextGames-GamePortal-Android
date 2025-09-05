package dk.nextgames.app.viewModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import dk.nextgames.app.data.Message
import dk.nextgames.app.data.ChatTheme
import dk.nextgames.app.data.chatThemes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.collections.get

/**
 * Handles all chat-related database operations.
 * Stores timestamps as ISO 8601 strings.
 */
class ChatViewModel(private val database: FirebaseDatabase) {
    private val chatRef = database.getReference("messages")
    private val userRef = database.getReference("users")

    // Holds the current user's selected theme name
    var currentThemeName: String = chatThemes.first().name
        private set

    /**
     * Converts a timestamp in milliseconds to ISO 8601 UTC string.
     */
    private fun formatTimestampIso8601(timeMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(timeMillis))
    }

    /**
     * Sends a message to Firebase, storing its timestamp in ISO 8601 format.
     */
    fun sendMessage(message: Message, onComplete: (() -> Unit)? = null) {
        val key = chatRef.push().key ?: return
        val isoTime = formatTimestampIso8601(message.timeStamp)
        val msgMap = mapOf(
            "text" to message.text,
            "userName" to message.userName,
            "timeStamp" to isoTime,
            "gameId" to message.gameId
        )
        chatRef.child(key).setValue(msgMap).addOnCompleteListener { onComplete?.invoke() }
    }

    /**
     * Parses an ISO 8601 UTC string to milliseconds.
     * Returns 0 if parsing fails.
     */
    private fun parseIso8601ToMillis(iso: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(iso)?.time ?: 0L
        } catch (_: Exception) {
            0L
        }
    }

    /**
     * Listens for new messages in the chat for a specific gameId.
     * Converts timestamps from ISO 8601 string or long as needed.
     */
    fun listenForMessages(gameId: String, onMessage: (Message) -> Unit): ChildEventListener {
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val map = snapshot.value as? Map<*, *> ?: return
                val text = map["text"] as? String ?: ""
                val userName = map["userName"] as? String ?: ""
                val gameIdValue = map["gameId"] as? String ?: ""
                val timeStampRaw = map["timeStamp"]
                val timeStamp = when (timeStampRaw) {
                    is Long -> timeStampRaw
                    is String -> timeStampRaw.toLongOrNull() ?: parseIso8601ToMillis(timeStampRaw)
                    else -> 0L
                }
                if (gameIdValue == gameId) {
                    onMessage(Message(text, userName, timeStamp, gameIdValue))
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        chatRef.addChildEventListener(listener)
        return listener
    }

    /**
     * Removes a previously registered message listener.
     */
    fun removeListener(listener: ChildEventListener) {
        chatRef.removeEventListener(listener)
    }

    /**
     * Deletes messages older than 1 hour (3600000 ms) from Firebase.
     * Handles both ISO string and long timestamp formats.
     */
    fun cleanOldMessages() {
        chatRef.get().addOnSuccessListener { snapshot ->
            val now = System.currentTimeMillis()
            snapshot.children.forEach { child ->
                val map = child.value as? Map<*, *> ?: return@forEach
                val timeStampRaw = map["timeStamp"]
                val timeStamp = when (timeStampRaw) {
                    is Long -> timeStampRaw
                    is String -> timeStampRaw.toLongOrNull() ?: parseIso8601ToMillis(timeStampRaw)
                    else -> 0L
                }
                if (now - timeStamp > 3600000) {
                    child.key?.let { chatRef.child(it).removeValue() }
                }
            }
        }
    }

    /**
     * Fetches all messages for a gameId, sorted by timestamp (oldest first).
     * Handles both ISO string and long timestamp formats.
     */
    fun fetchAllMessages(gameId: String, onMessages: (List<Message>) -> Unit) {
        chatRef.get().addOnSuccessListener { snapshot ->
            val messages = snapshot.children.mapNotNull { child ->
                val map = child.value as? Map<*, *> ?: return@mapNotNull null
                val text = map["text"] as? String ?: ""
                val userName = map["userName"] as? String ?: ""
                val gameIdValue = map["gameId"] as? String ?: ""
                val timeStampRaw = map["timeStamp"]
                val timeStamp = when (timeStampRaw) {
                    is Long -> timeStampRaw
                    is String -> timeStampRaw.toLongOrNull() ?: parseIso8601ToMillis(timeStampRaw)
                    else -> 0L
                }
                if (gameIdValue == gameId) Message(text, userName, timeStamp, gameIdValue) else null
            }.sortedBy { it.timeStamp }
            onMessages(messages)
        }
    }

    /**
     * Saves the selected chat theme for the current user in Firebase.
     */
    fun saveUserTheme(themeName: String, onComplete: (() -> Unit)? = null) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        userRef.child(userId).child("chatTheme").setValue(themeName)
            .addOnCompleteListener { onComplete?.invoke() }
        currentThemeName = themeName
    }

    /**
     * Loads the user's selected chat theme from Firebase and sets it.
     * Calls onTheme with the ChatTheme object (defaults to Light if not found).
     */
    fun loadUserTheme(onTheme: (ChatTheme) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        userRef.child(userId).child("chatTheme").get().addOnSuccessListener { snapshot ->
            val themeName = snapshot.value as? String ?: chatThemes.first().name
            currentThemeName = themeName
            val theme = chatThemes.find { it.name == themeName } ?: chatThemes.first()
            onTheme(theme)
        }.addOnFailureListener {
            // Fallback to default theme
            currentThemeName = chatThemes.first().name
            onTheme(chatThemes.first())
        }
    }

    /**
     * Returns the current ChatTheme object.
     */
    fun getCurrentTheme(): ChatTheme {
        return chatThemes.find { it.name == currentThemeName } ?: chatThemes.first()
    }
}