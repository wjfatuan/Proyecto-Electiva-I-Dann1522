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
import com.example.flickzy.LoginReqUiState
import com.example.flickzy.ui.components.Loader
import com.example.flickzy.ui.components.LoginForm
import com.example.flickzy.ui.theme.FlickzyTheme

@Composable
fun LoginScreen(
    mailValue: String,
    passwordValue: String,
    onMailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onSendButtonClick: () -> Unit,
    loginReqUiState: LoginReqUiState,
    onSuccess: () -> Unit,
    onDissmissError: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ){
        LoginForm(
            mailValue = mailValue,
            passwordValue = passwordValue,
            onMailValueChange = onMailValueChange,
            onPasswordValueChange = onPasswordValueChange,
            onSendButtonClick = onSendButtonClick,
            onLinkClick = onLinkClick
        )
        when(loginReqUiState){
            is LoginReqUiState.NotSended -> {}
            is LoginReqUiState.Loading -> {
                Loader()
            }
            is LoginReqUiState.Success -> {
                AlertDialog(
                    onDismissRequest = onSuccess,
                    icon = {
                        Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null)
                    },
                    title = {
                        Text("Sesión Iniciada")
                    },
                    text = {
                        Text(
                            loginReqUiState.message
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = onSuccess
                        ) {
                            Text("Ok")
                        }
                    }
                )
            }
            is LoginReqUiState.Error -> {
                AlertDialog(
                    onDismissRequest = onDissmissError,
                    icon = {
                        Icon(Icons.Outlined.ErrorOutline, contentDescription = null)
                    },
                    title = {
                        Text("Error al Iniciar Sesión")
                    },
                    text = {
                        Text(
                            loginReqUiState.error
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = onDissmissError
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
fun PreviewLoginScreen(){
    FlickzyTheme {
        LoginScreen("", "", {}, {}, {}, LoginReqUiState.NotSended, {}, {}, {})
    }
}