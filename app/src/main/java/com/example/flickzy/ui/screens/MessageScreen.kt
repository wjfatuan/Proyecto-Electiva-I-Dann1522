package com.example.flickzy.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flickzy.MessageReqUiState
import com.example.flickzy.ui.components.Loader
import com.example.flickzy.ui.components.MessageList
import com.example.flickzy.ui.theme.FlickzyTheme

@Composable
fun MessageScreen(
    myId: Int,
    textContent: String,
    onUpdateTextField: (String) -> Unit,
    onSendButtonClick: () -> Unit,
    messageReqUiState: MessageReqUiState,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ){
        Scaffold (
            bottomBar = {
                val scrollState = rememberScrollState()
                BottomAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    TextField(
                        value = textContent,
                        onValueChange = onUpdateTextField,
                        placeholder = { Text("Escribe un mensaje...") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .verticalScroll(scrollState)
                    )
                    Button(onClick = onSendButtonClick) {
                        Text("Enviar")
                    }
                }
            }
        ){ innerpadding ->
            Box(
                modifier = Modifier
                    .padding(innerpadding)
            ){
                when(messageReqUiState){
                    is MessageReqUiState.Loading -> Loader()
                    is MessageReqUiState.Success -> {
                        MessageList(
                            messageList = messageReqUiState.chats,
                            myId = myId
                        )
                    }
                    is MessageReqUiState.Error -> {
                        Text(messageReqUiState.error)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewMessageScreen(){
    FlickzyTheme {
        MessageScreen(0, "", {}, {}, messageReqUiState = MessageReqUiState.Error("Error"))
    }
}