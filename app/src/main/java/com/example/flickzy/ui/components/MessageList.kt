package com.example.flickzy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flickzy.data.Message
import com.example.flickzy.ui.theme.FlickzyTheme

@Composable
fun MessageList(
    messageList: List<Message>,
    myId: Int,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ) {
        val listState = rememberLazyListState()
        LazyColumn (
            state = listState
        ){
            items(messageList) { message ->
                MessageElement(
                    content = message.content,
                    time = message.timestamp,
                    myId = myId,
                    otherId = message.idSender
                )
            }
        }
        LaunchedEffect(messageList.size) {
            if(messageList.size - 1 > 0){
                listState.animateScrollToItem(messageList.size - 1, scrollOffset = 0)
            }
        }
    }
}

@Composable
fun MessageElement(
    content: String,
    time: String,
    myId: Int,
    otherId: Int,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .padding(8.dp)
            .background(
                if (otherId == myId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = content,
                modifier = Modifier.padding(bottom = 4.dp),
                color = if (otherId == myId) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = (if (otherId == myId) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onTertiary).copy(0.6f),
                    )
            }
        }
    }
}

@Preview
@Composable
fun PreviewMessageList(){
    FlickzyTheme {
        MessageElement("Hola", "12:40", 0, 0)
    }
}

@Preview
@Composable
fun PreviewMessageList2(){
    FlickzyTheme {
        MessageElement("Hola", "10:50", 0, 1)
    }
}