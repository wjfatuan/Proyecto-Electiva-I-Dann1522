package com.example.flickzy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flickzy.data.Chat
import com.example.flickzy.ui.theme.FlickzyTheme
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ChatList(
    chats: List<Chat>,
    onClickAction: (Int) -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ){
        LazyColumn {
            items(chats){ chat->
                ChatElement(
                    userName = chat.otherUserName,
                    previewMessage = chat.lastMessageText ?: "",
                    lastMessageTime = chat.lastMessageTime ?: "",
                    onClickAction = {
                        onClickAction(chat.id)
                    }
                )
            }
        }
    }
}

@Composable
fun ChatElement(
    userName: String,
    previewMessage: String,
    lastMessageTime: String,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier
){
    fun formatTime(time: String) : String{
        val formatter = DateTimeFormatter.RFC_1123_DATE_TIME
        val gmtDateTime = ZonedDateTime.parse(time, formatter)
        val localDateTime = gmtDateTime.withZoneSameInstant(ZoneId.systemDefault())
        val now = ZonedDateTime.now()
        val hoursDifference = Duration.between(localDateTime, now).toHours()
        val outputFormatter = if (hoursDifference <= 24) {
            DateTimeFormatter.ofPattern("HH:mm")
        } else {
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        }
        return localDateTime.format(outputFormatter)
    }
    Box(
        modifier = modifier
            .clickable(
                onClick = onClickAction
            )
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            ) {
                Text(
                    userName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    previewMessage,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                if (lastMessageTime.isNotEmpty()) formatTime(lastMessageTime) else "",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun PreviewChatList(){
    FlickzyTheme {
        ChatList(
            listOf(
                Chat(
                    0,
                    "Mi texto",
                    "Fri, 02 May 2025 10:58:05 GMT",
                    "Cristo"
                )
            ),
            {}
        )
    }
}