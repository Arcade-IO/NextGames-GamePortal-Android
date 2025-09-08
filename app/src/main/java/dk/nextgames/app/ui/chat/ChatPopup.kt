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
import androidx.compose.material.icons.filled.Settings
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
import dk.nextgames.app.data.ChatTheme
import dk.nextgames.app.data.chatThemes
import androidx.compose.foundation.layout.imePadding

/**
 * ChatPopup is a composable for displaying and sending chat messages.
 * Handles message input, sending, live updating, and scroll-to-bottom.
 * Applies user's selected chat theme.
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

    // State for chat messages and input
    val messages = remember { mutableStateListOf<Message>() }
    var input by remember { mutableStateOf("") }
    var listener by remember { mutableStateOf<ChildEventListener?>(null) }
    val scrollState = rememberScrollState()

    // State for theme dialog and current theme
    var showThemeDialog by remember { mutableStateOf(false) }
    var chatTheme by remember { mutableStateOf(chatThemes.first()) }

    // Load user's theme when chat opens
    LaunchedEffect(Unit) {
        safeChatRepo.loadUserTheme { theme ->
            chatTheme = theme
        }
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

    // Cleanup: remove listener
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

    // Popup overlay for chat
    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(), // juster for tastatur
        contentAlignment = Alignment.Center
    ) {
        // Main chat container with ContainerCard
        ContainerCard(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(chatTheme.backgroundColor) // keep background theme inside
                    .padding(18.dp)
            ) {
                // Header with title, settings, and close button
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Game Chat",
                        style = MaterialTheme.typography.titleLarge,
                        color = chatTheme.textColor
                    )
                    Row {
                        IconButton(onClick = { showThemeDialog = true }) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = "Settings",
                                tint = chatTheme.textColor
                            )
                        }
                        IconButton(onClick = onClose) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Close Chat",
                                tint = chatTheme.textColor
                            )
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                // Message list
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
                                    color = if (isOwn) chatTheme.bubbleColorOwn else chatTheme.bubbleColorOther,
                                    shape = MaterialTheme.shapes.medium,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .padding(vertical = 2.dp, horizontal = 4.dp)
                                        .widthIn(max = 260.dp)
                                ) {
                                    Column(Modifier.padding(10.dp)) {
                                        Text(
                                            msg.userName,
                                            color = if (isOwn) chatTheme.bubbleTextColorOwn else chatTheme.bubbleTextColorOther,
                                            style = MaterialTheme.typography.labelMedium,
                                            modifier = Modifier.padding(bottom = 2.dp)
                                        )
                                        Text(
                                            msg.text,
                                            color = if (isOwn) chatTheme.bubbleTextColorOwn else chatTheme.bubbleTextColorOther,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                // Input row
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
                        placeholder = { Text("Write a message...", color = chatTheme.textColor) },
                        shape = MaterialTheme.shapes.medium,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = chatTheme.bubbleColorOwn,
                            unfocusedBorderColor = chatTheme.textColor,
                            cursorColor = chatTheme.bubbleColorOwn
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = chatTheme.textColor)
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
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = chatTheme.bubbleColorOwn
                        )
                    }
                }
            }

            // Theme selection dialog
            if (showThemeDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showThemeDialog = false },
                    containerColor = Color(0xFF1F1F1F),
                    title = {
                        Text("Theme Settings", style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )

                    },
                    text = {
                        Column {
                            Text("Choose Chat Theme", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))
                            chatThemes.forEach { theme ->
                                Button(
                                    onClick = {
                                        chatTheme = theme
                                        safeChatRepo.saveUserTheme(theme.name) {
                                            showThemeDialog = false
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                        containerColor = theme.backgroundColor,
                                        contentColor = theme.textColor
                                    )
                                ) {
                                    Text(theme.name)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showThemeDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }

        }

        // Auto-scroll to bottom when new messages arrive
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }
}