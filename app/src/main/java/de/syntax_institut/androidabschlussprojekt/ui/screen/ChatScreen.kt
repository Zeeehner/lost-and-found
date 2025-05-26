package de.syntax_institut.androidabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatMessage
import de.syntax_institut.androidabschlussprojekt.ui.viewmodel.ChatViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatDetailScreen(itemId: String, userId: String, userName: String) {
    val chatViewModel: ChatViewModel = koinViewModel()
    val messages by chatViewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    LaunchedEffect(itemId) {
        chatViewModel.loadMessages(itemId)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages.size) { index ->
                val msg = messages[index]
                Text("${msg.userName}: ${msg.message}")
            }
        }

        Row(Modifier.fillMaxWidth()) {
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f).padding(8.dp)
            )
            Button(onClick = {
                if (input.isNotBlank()) {
                    chatViewModel.sendMessage(
                        itemId,
                        ChatMessage(
                            id = "",
                            itemId = itemId,
                            userId = userId,
                            userName = userName,
                            message = input,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    input = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}