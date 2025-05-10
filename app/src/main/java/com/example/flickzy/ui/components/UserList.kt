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
import com.example.flickzy.data.User
import com.example.flickzy.ui.theme.FlickzyTheme
import java.util.Locale


@Composable
fun UserList(
    users: List<User>,
    onClickAction: (id: Int) -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ){
        LazyColumn {
            items(users){ user ->
                UserElement(
                    userName = user.name.capitalize(Locale.ROOT),
                    mail = user.mail,
                    onClickAction = {
                        onClickAction(user.id)
                    }
                )
            }
        }
    }
}

@Composable
fun UserElement(
    userName: String,
    mail: String,
    onClickAction : () -> Unit,
    modifier: Modifier = Modifier
){
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
                    mail,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewUserList(){
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