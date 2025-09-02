// app/src/main/java/dk/nextgames/app/ui/chat/ChatPopup.kt
package dk.nextgames.app.ui.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.database.ChildEventListener
import dk.nextgames.app.data.Message
import dk.nextgames.app.viewModel.ChatViewModel
import dk.nextgames.app.ui.common.ContainerCard

/**
 * ChatPopup is a composable for displaying and sending chat messages.
 * Handles message input, sending, live updating, and scroll-to-bottom.
 */
@Composable
fun ChatPopup(
    gameId: String,
    userName: String?,
    chatViewModel: ChatViewModel?,
    onClose: () -> Unit
) {
    val safeGameId = gameId.ifBlank { "unknown" }
    val safeUserName = userName ?: "Anonymous"
    val safeChatRepo = chatViewModel ?: run {
        // Fallback: vis uinitialiseret chat i DIN kasse (ContainerCard)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xAA222222)),
            contentAlignment = Alignment.Center
        ) {
            ContainerCard(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Chat unavailable", style = MaterialTheme.typography.titleMedium)
                    Text("Chat service is not initialized.")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onClose) { Text("Close") }
                }
            }
        }
        return
    }

    // State
    val messages = remember { mutableStateListOf<Message>() }
    var input by remember { mutableStateOf("") }
    var listener by remember { mutableStateOf<ChildEventListener?>(null) }
    val scrollState = rememberScrollState()

    // Init: hent historik og lyt på nye beskeder
    LaunchedEffect(Unit) {
        try {
            safeChatRepo.cleanOldMessages()
            safeChatRepo.fetchAllMessages(safeGameId) { fetchedMessages ->
                messages.clear()
                messages.addAll(fetchedMessages)
            }
            listener = safeChatRepo.listenForMessages(safeGameId) { msg ->
                messages.add(msg)
            }
        } catch (e: Exception) {
            Log.e("ChatPopup", "Error initializing chat: ", e)
        }
    }

    // Cleanup: fjern lytter
    DisposableEffect(Unit) {
        onDispose {
            listener?.let {
                try {
                    safeChatRepo.removeListener(it)
                } catch (e: Exception) {
                    Log.e("ChatPopup", "Error removing listener: ", e)
                }
            }
        }
    }

    // Popup overlay + DIN kasse som container for hele chatvinduet
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        ContainerCard(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Game Chat", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Chat")
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                // Beskedliste
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(Modifier.verticalScroll(scrollState)) {
                        messages.forEach { msg ->
                            val isOwn = msg.userName == safeUserName
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    color = if (isOwn) Color(0xFF1976D2) else Color(0xFFE3E3E3),
                                    shape = MaterialTheme.shapes.medium,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .padding(vertical = 2.dp, horizontal = 4.dp)
                                        .widthIn(max = 260.dp)
                                ) {
                                    Column(Modifier.padding(10.dp)) {
                                        Text(
                                            msg.userName,
                                            color = if (isOwn) Color.White.copy(alpha = 0.9f) else Color.DarkGray,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                        Text(
                                            msg.text,
                                            color = if (isOwn) Color.White else Color.Black,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                // Input række
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = { Text("Write a message...") },
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(
                        onClick = {
                            if (input.isNotBlank()) {
                                try {
                                    val msg = Message(
                                        text = input,
                                        userName = safeUserName,
                                        timeStamp = System.currentTimeMillis(),
                                        gameId = safeGameId
                                    )
                                    safeChatRepo.sendMessage(msg)
                                    input = ""
                                } catch (e: Exception) {
                                    Log.e("ChatPopup", "Error sending message: ", e)
                                }
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                    }
                }
            }
        }
    }

    // Auto-scroll til bund ved nye beskeder
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
}
