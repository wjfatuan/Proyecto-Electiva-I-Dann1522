package com.example.flickzy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flickzy.ui.theme.FlickzyTheme

@Composable
fun LoginForm(
    mailValue: String,
    passwordValue: String,
    onMailValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    onSendButtonClick: () -> Unit,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier
){
    var viewPasswordButton by remember { mutableStateOf(true) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.
        fillMaxSize()
    ){
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.75f)
        ){
            Text(
                text = "Inicia Sesión",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = mailValue,
                onValueChange = onMailValueChange,
                label = {
                    Text("Correo Electronico")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = passwordValue,
                onValueChange = onPasswordValueChange,
                label = {
                    Text("Contraseña")
                },
                singleLine = true,
                visualTransformation = if (viewPasswordButton) PasswordVisualTransformation() else VisualTransformation.None,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewPasswordButton = !viewPasswordButton
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RemoveRedEye,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSendButtonClick
            ) {
                Text("Iniciar Sesion")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("No tienes una cuenta?")
            Text(
                text = "Registrate",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(
                        onClick = onLinkClick
                    )
            )
        }
    }
}

@Preview
@Composable
fun PreviewLoginForm(){
    FlickzyTheme {
        LoginForm("","",{}, {}, {}, {})
    }
}
