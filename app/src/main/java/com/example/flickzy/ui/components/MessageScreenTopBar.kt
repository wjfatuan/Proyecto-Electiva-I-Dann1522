package com.example.flickzy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flickzy.ui.theme.FlickzyTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreenTopBar(userName: String, userState: Int, modifier: Modifier = Modifier){
    TopAppBar(
        title = {
            Column {
                Text(
                    text = userName.capitalize(Locale.ROOT),
                    style = MaterialTheme.typography.titleLarge
                )
                if (userState != 0){
                    Text(
                        text = "En línea",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun PreviewMessageScreenTopBar(){
    FlickzyTheme {
        MessageScreenTopBar("Cristo", 1)
    }
}