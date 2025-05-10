package com.example.flickzy.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flickzy.TopBarState
import com.example.flickzy.ui.theme.FlickzyTheme

@Composable
fun TopBar(
    topBarState: TopBarState,
    modifier: Modifier = Modifier
){
    when (topBarState){
        is TopBarState.MainScreen -> {
            ChatScreenTopBar(modifier)
        }
        is TopBarState.MessageScreen -> {
            MessageScreenTopBar(
                topBarState.userName,
                topBarState.userState,
                modifier
            )
        }
    }
}

@Preview
@Composable
fun PreviewTopBar(){
    FlickzyTheme {
        TopBar(topBarState = TopBarState.MainScreen)
    }
}