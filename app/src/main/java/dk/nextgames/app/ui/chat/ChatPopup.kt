package dk.nextgames.app.ui.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.database.ChildEventListener
import dk.nextgames.app.data.Message
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.automirrored.filled.Send

/**
 * ChatPopup is a composable for displaying and sending chat messages.
 * Handles message input, sending, live updating, and scroll-to-bottom.
 */
@Composable
fun ChatPopup(
    gameId: String,
    userName: String?,
    chatRepository: ChatRepository?,
    onClose: () -> Unit
) {
    val safeGameId = gameId.ifBlank { "unknown" }
    val safeUserName = userName ?: "Anonymous"
    val safeChatRepo = chatRepository ?: run {
        // Show unavailable UI if repo is not provided
        Box(
            Modifier.fillMaxSize().background(Color(0xAA222222)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.background(Color.White).padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Chat unavailable", style = MaterialTheme.typography.titleMedium)
                Text("Chat service is not initialized.", color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onClose) { Text("Close") }
            }
        }
        return
    }

    // State for messages and input
    val messages = remember { mutableStateListOf<Message>() }
    var input by remember { mutableStateOf("") }
    var listener by remember { mutableStateOf<ChildEventListener?>(null) }
    val scrollState = rememberScrollState()

    // On first composition, set up listeners and fetch history
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

    // Clean up the listener when popup closes
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


    // Main chat popup UI
    Box(
        Modifier.fillMaxSize().background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = MaterialTheme.shapes.large, color = Color.White,
            tonalElevation = 12.dp,
            shadowElevation = 24.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .background(Color.White)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .background(Color.White)
            ) {
                // Header bar
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Game Chat", style = MaterialTheme.typography.titleLarge, color = Color.Black)
                    IconButton(onClick = onClose) {
                        Icon(Icons.Filled.Close, contentDescription = "Close Chat", tint = Color.Black)
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                // Chat box messages (scrollable)
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.White, MaterialTheme.shapes.medium)
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
                                            color = if (isOwn) Color.White.copy(alpha = 0.9f) else Color.Gray,
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

                // User input area
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Transparent)
                            .padding(end = 8.dp),
                        placeholder = { Text("Write a message...", color = Color.DarkGray) },
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = Color(0xFF1976D2)
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.Black)
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
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color(0xFF1976D2))
                    }
                }
            }
        }
    }

    // Auto-scroll to the bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }
}