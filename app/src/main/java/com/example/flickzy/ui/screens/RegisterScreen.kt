package com.example.flickzy.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.flickzy.RegisterReqUiState
import com.example.flickzy.ui.components.Loader
import com.example.flickzy.ui.components.RegisterForm
import com.example.flickzy.ui.theme.FlickzyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    nameValue: String,
    mailValue: String,
    passwordValue: String,
    onNameValueChange: (String) -> Unit,
    onMailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onSendButtonClick: () -> Unit,
    onLinkClick: () -> Unit,
    registerReqUiState: RegisterReqUiState,
    onSuccess: () -> Unit,
    onDissmissError: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
    ) {
        RegisterForm(
            nameValue = nameValue,
            mailValue = mailValue,
            passwordValue = passwordValue,
            onNameValueChange = onNameValueChange,
            onMailValueChange = onMailValueChange,
            onPasswordValueChange = onPasswordValueChange,
            onSendButtonClick = onSendButtonClick,
            onLinkClick = onLinkClick
        )
        when(registerReqUiState){
            is RegisterReqUiState.NotSended -> {}
            is RegisterReqUiState.Loading -> {
                Loader()
            }
            is RegisterReqUiState.Success -> {
                AlertDialog(
                    onDismissRequest = onSuccess,
                    icon = {
                        Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null)
                    },
                    title = {
                        Text("Registro exitoso")
                    },
                    text = {
                        Text(
                            registerReqUiState.message
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = onSuccess
                        ) {
                            Text("Iniciar Sesion")
                        }
                    }
                )
            }
            is RegisterReqUiState.Error -> {
                AlertDialog(
                    onDismissRequest = onDissmissError,
                    icon = {
                        Icon(Icons.Outlined.ErrorOutline, contentDescription = null)
                    },
                    title = {
                        Text("Error al registrarse")
                    },
                    text = {
                        Text(
                            registerReqUiState.error
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
fun PreviewRegisterScreen(){
    FlickzyTheme {
        RegisterScreen("", "", "", {}, {}, {}, {},{}, RegisterReqUiState.Error("El usuario ya existe"), {}, {})
    }
}