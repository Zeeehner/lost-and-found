package de.syntax_institut.androidabschlussprojekt.ui.component.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.syntax_institut.androidabschlussprojekt.data.local.model.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageList(message: ChatMessage, currentUserId: String) {
    val isCurrentUser = message.userId == currentUserId
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    val formattedTime = message.timestamp?.let {
        val date = Date(it)
        val today = Calendar.getInstance()
        val messageDate = Calendar.getInstance().apply { time = date }

        if (today.get(Calendar.YEAR) == messageDate.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == messageDate.get(Calendar.DAY_OF_YEAR)) {
            "Today, ${timeFormatter.format(date)}"
        } else {
            "${dateFormatter.format(date)}, ${timeFormatter.format(date)}"
        }
    } ?: ""

    MessageBubble(
        message = message.message,
        userName = message.userName,
        timestamp = formattedTime,
        isCurrentUser = isCurrentUser,
        modifier = Modifier.padding(
            start = if (isCurrentUser) 64.dp else 8.dp,
            end = if (isCurrentUser) 8.dp else 64.dp
        )
    )
}