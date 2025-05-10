package com.example.flickzy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flickzy.network.MessageApi
import com.example.flickzy.storage.TokenStore
import com.example.flickzy.ui.components.TopBar
import com.example.flickzy.ui.screens.ChatScreen
import com.example.flickzy.ui.screens.LoginScreen
import com.example.flickzy.ui.screens.MessageScreen
import com.example.flickzy.ui.screens.NewChatScreen
import com.example.flickzy.ui.screens.RegisterScreen
import com.example.flickzy.ui.theme.FlickzyTheme
import kotlinx.coroutines.delay

enum class Screens {
    LOAD_SCREEN,
    CHAT_SCREEN,
    LOGIN_SCREEN,
    REGISTER_SCREEN,
    NEW_CHAT_SCREEN,
    MESSAGE_SCREEN
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenStore.init(this)
        enableEdgeToEdge()
        setContent {
            FlickzyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
    val flickzyViewModel : FlickzyViewModel = viewModel()
    val registerFormUI = flickzyViewModel.registerFormUI.collectAsState()
    val loginFormUI = flickzyViewModel.loginFormUI.collectAsState()
    val flickzyState = flickzyViewModel.flickzyState.collectAsState()
    val messageFormUI = flickzyViewModel.messageFormUI.collectAsState()
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopBar(
                topBarState = flickzyViewModel.topBarState
            )
        },
        modifier = modifier
    ){ innerPadding ->
        Box(
            modifier= Modifier
                .padding(innerPadding)
        ){
            NavHost(
                navController = navController,
                startDestination = Screens.REGISTER_SCREEN.name
            ) {
                composable(
                    route = Screens.REGISTER_SCREEN.name
                ) {
                    RegisterScreen(
                        nameValue = registerFormUI.value.name,
                        mailValue = registerFormUI.value.mail,
                        passwordValue = registerFormUI.value.password,
                        onNameValueChange = { name ->
                            flickzyViewModel.updateRegisterName(name)
                        },
                        onMailValueChange = { mail ->
                            flickzyViewModel.updateRegisterMail(mail)
                        },
                        onPasswordValueChange = { pass ->
                            flickzyViewModel.updateRegisterPassword(pass)
                        },
                        onSendButtonClick = {
                            flickzyViewModel.onClickSendRegister()
                        },
                        registerReqUiState = flickzyViewModel.registerReqUiState,
                        onSuccess = {
                            navController.navigate(Screens.LOGIN_SCREEN.name)
                        },
                        onDissmissError = {
                            flickzyViewModel.onRegisterErrorCloseAlert()
                        },
                        onLinkClick = {
                            navController.navigate(Screens.LOGIN_SCREEN.name)
                        }
                    )
                }
                composable (
                    route = Screens.LOGIN_SCREEN.name
                ) {
                    LoginScreen(
                        mailValue = loginFormUI.value.mail,
                        passwordValue = loginFormUI.value.pass,
                        onMailValueChange = { mail ->
                            flickzyViewModel.updateLoginMail(mail)
                        },
                        onPasswordValueChange = { password ->
                            flickzyViewModel.updateLoginPassword(password)
                        },
                        onSendButtonClick = {
                            flickzyViewModel.onClickSendLogin()
                        },
                        loginReqUiState = flickzyViewModel.loginReqUiState,
                        onSuccess = {
                            navController.navigate(Screens.CHAT_SCREEN.name)
                        },
                        onDissmissError = {
                            flickzyViewModel.onLoginErrorCloseAlert()
                        },
                        onLinkClick = {
                            navController.navigate(Screens.REGISTER_SCREEN.name)
                        }
                    )
                }
                composable (
                    route = Screens.CHAT_SCREEN.name
                ) {
                    LaunchedEffect(Unit) {
                        flickzyViewModel.resetTopBar()
                        while (true){
                            flickzyViewModel.getChats()
                            delay(1000)
                        }
                    }
                    ChatScreen(
                        onAddChatClick = {
                            flickzyViewModel.getUsers()
                            navController.navigate(Screens.NEW_CHAT_SCREEN.name)
                        },
                        onChatSelection = { id ->
                            navController.navigate(Screens.MESSAGE_SCREEN.name)
                            flickzyViewModel.getUserNameAndStatus(id)
                            flickzyViewModel.getMessages(id)
                        },
                        chatUiState = flickzyViewModel.chatUiState
                    )
                }
                composable (
                    route = Screens.NEW_CHAT_SCREEN.name
                ){
                    NewChatScreen(
                        addChatUiState = flickzyViewModel.addChatUiState,
                        newChatReqUiState = flickzyViewModel.newChatReqUiState,
                        onClickAction = { id ->
                            flickzyViewModel.newChat(id)
                        },
                        onSuccessNewchat = {
                            navController.navigate(Screens.CHAT_SCREEN.name)
                            flickzyViewModel.onNewChatSuccessResetState()
                        },
                        onErrorNewChat = {
                            flickzyViewModel.onNewChatErrorCloseAlert()
                        }
                    )
                }
                composable(
                    route = Screens.MESSAGE_SCREEN.name
                ){
                    LaunchedEffect(Unit) {
                        while (true){
                            flickzyViewModel.getMessages(flickzyState.value.selectedChatId)
                            delay(1000)
                        }
                    }
                    MessageScreen(
                        flickzyState.value.loggedUserId,
                        textContent = messageFormUI.value.content,
                        onUpdateTextField = { message ->
                            flickzyViewModel.updateMessageContent(message)
                        },
                        onSendButtonClick = {
                            flickzyViewModel.sendMessage()
                        },
                        flickzyViewModel.messageReqUiState
                    )
                }
            }
        }
    }
}