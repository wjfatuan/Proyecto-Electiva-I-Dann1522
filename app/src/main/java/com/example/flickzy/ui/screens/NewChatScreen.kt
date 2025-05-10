package com.example.flickzy.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flickzy.AddChatUiState
import com.example.flickzy.NewChatReqUiState
import com.example.flickzy.ui.components.Loader
import com.example.flickzy.ui.components.UserList
import com.example.flickzy.ui.theme.FlickzyTheme

@Composable
fun NewChatScreen(
    addChatUiState: AddChatUiState,
    newChatReqUiState: NewChatReqUiState,
    onClickAction: (Int) -> Unit,
    onSuccessNewchat: () -> Unit,
    onErrorNewChat: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ){
        when(addChatUiState){
            is AddChatUiState.Loading -> Loader()
            is AddChatUiState.Success -> {
                UserList(
                    addChatUiState.users,
                    onClickAction = onClickAction
                )
            }
            is AddChatUiState.Error -> {
                Text(addChatUiState.error)
            }
        }
        when(newChatReqUiState){
            is NewChatReqUiState.NotSelected -> {}
            is NewChatReqUiState.Loading -> Loader()
            is NewChatReqUiState.Success -> {
                AlertDialog(
                    onDismissRequest = onSuccessNewchat,
                    icon = {
                        Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null)
                    },
                    title = {
                        Text("Chat Creado Correctamente")
                    },
                    text = {
                        Text(
                            newChatReqUiState.message.message
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = onSuccessNewchat
                        ) {
                            Text("Volver a Inicio")
                        }
                    }
                )
            }
            is NewChatReqUiState.Error -> {
                AlertDialog(
                    onDismissRequest = onErrorNewChat,
                    icon = {
                        Icon(Icons.Outlined.ErrorOutline, contentDescription = null)
                    },
                    title = {
                        Text("Error al Crear Chat")
                    },
                    text = {
                        Text(
                            newChatReqUiState.error
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = onErrorNewChat
                        ) {
                            Text("Volver a intentarlo")
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewNewChatScreen(){
    FlickzyTheme {
        NewChatScreen(AddChatUiState.Loading, NewChatReqUiState.NotSelected, {}, {}, {})
    }
}