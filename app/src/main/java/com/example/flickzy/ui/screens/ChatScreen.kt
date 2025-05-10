package com.example.flickzy.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flickzy.ChatUiState
import com.example.flickzy.data.Chat
import com.example.flickzy.ui.components.ChatList
import com.example.flickzy.ui.components.Loader
import com.example.flickzy.ui.theme.FlickzyTheme

@Composable
fun ChatScreen(
    onAddChatClick: () -> Unit,
    onChatSelection: (Int) -> Unit,
    chatUiState: ChatUiState,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ){
        Scaffold(
            floatingActionButton = {
                FilledIconButton(onClick = onAddChatClick) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                }
            }
        ) { innerPadding ->
            Box (
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                when (chatUiState) {
                    is ChatUiState.Loading -> Loader()
                    is ChatUiState.Success -> {
                        ChatList(
                            chats = chatUiState.chats,
                            onClickAction = onChatSelection
                        )
                    }
                    is ChatUiState.Error -> {
                        Text("No se han podido obtener los chats: " + chatUiState.error)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewChatScreen(){
    FlickzyTheme {
        ChatScreen(
            {},
            {},
            chatUiState = ChatUiState.Success(
                listOf(
                    Chat(
                        0,
                        "Mi texto",
                        "Fri, 02 May 2025 10:58:05 GMT",
                        "Cristo"
                    )
                )
            )
        )
    }
}