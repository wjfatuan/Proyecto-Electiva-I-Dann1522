package com.example.flickzy

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flickzy.network.MessageApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.flickzy.data.Chat
import com.example.flickzy.data.Error
import com.example.flickzy.data.ChatRequest
import com.example.flickzy.data.FlickzyState
import com.example.flickzy.data.LoginFormUI
import com.example.flickzy.data.LoginRequest
import com.example.flickzy.data.LoginResponse
import com.example.flickzy.data.Message
import com.example.flickzy.data.MessageFormUI
import com.example.flickzy.data.MessageRequest
import com.example.flickzy.data.NewChatRequest
import com.example.flickzy.data.RegisterFormUI
import com.example.flickzy.data.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.security.MessageDigest
import kotlinx.serialization.json.Json
import com.example.flickzy.data.RegisterResponse
import com.example.flickzy.data.SendMessageRequest
import com.example.flickzy.data.Success
import com.example.flickzy.data.TopUser
import com.example.flickzy.data.User
import com.example.flickzy.storage.TokenStore

sealed interface TopBarState{
    object MainScreen : TopBarState
    data class MessageScreen(val userName: String, val userState: Int) : TopBarState
}

sealed interface AddChatUiState {
    data class Success(val users: List<User>) : AddChatUiState
    data class Error(val error: String) : AddChatUiState
    object Loading : AddChatUiState
}

sealed interface NewChatReqUiState{
    data class Success(val message: com.example.flickzy.data.Success): NewChatReqUiState
    data class Error(val error: String) : NewChatReqUiState
    object Loading: NewChatReqUiState
    object NotSelected: NewChatReqUiState
}

sealed interface RegisterReqUiState{
    data class Success(val message: String): RegisterReqUiState
    data class Error(val error: String): RegisterReqUiState
    object Loading: RegisterReqUiState
    object NotSended: RegisterReqUiState
}

sealed interface LoginReqUiState{
    data class Success(val message: String): LoginReqUiState
    data class Error(val error: String) : LoginReqUiState
    object Loading: LoginReqUiState
    object NotSended : LoginReqUiState
}

sealed interface ChatUiState{
    data class Success(val chats: List<Chat>) : ChatUiState
    data class Error(val error: String) : ChatUiState
    object Loading: ChatUiState
}

sealed interface MessageReqUiState{
    data class Success(val chats: List<Message>) : MessageReqUiState
    data class Error(val error: String) : MessageReqUiState
    object Loading: MessageReqUiState
}

sealed interface SendMessageUIState{
    object NotSend: SendMessageUIState
    data class Success(val message: com.example.flickzy.data.Success) : SendMessageUIState
    data class Error(val error: String)
}

class FlickzyViewModel : ViewModel(){
    val json = Json { ignoreUnknownKeys = true }

    private val _registerFormUI = MutableStateFlow(RegisterFormUI())
    private val _loginFormUI = MutableStateFlow(LoginFormUI())
    private val _flickzyState = MutableStateFlow(FlickzyState())
    private val _messageFormUI = MutableStateFlow(MessageFormUI())
    val registerFormUI  = _registerFormUI.asStateFlow()
    val loginFormUI = _loginFormUI.asStateFlow()
    val flickzyState = _flickzyState.asStateFlow()
    val messageFormUI = _messageFormUI.asStateFlow()
    var addChatUiState : AddChatUiState by mutableStateOf(AddChatUiState.Loading)
        private set
    var registerReqUiState : RegisterReqUiState by mutableStateOf(RegisterReqUiState.NotSended)
        private set
    var loginReqUiState : LoginReqUiState by mutableStateOf(LoginReqUiState.NotSended)
    var chatUiState : ChatUiState by mutableStateOf(ChatUiState.Loading)
    var messageReqUiState : MessageReqUiState by mutableStateOf(MessageReqUiState.Loading)
    var topBarState : TopBarState by mutableStateOf(TopBarState.MainScreen)
    var newChatReqUiState: NewChatReqUiState by mutableStateOf(NewChatReqUiState.NotSelected)
    var sendMessageUIState: SendMessageUIState by mutableStateOf(SendMessageUIState.NotSend)

    override fun onCleared() {
        viewModelScope.launch {
            try {
                val requestHeader = "Bearer " + flickzyState.value.onlineJWT
                val response = MessageApi.retrofitService.logout(
                    requestHeader
                )
                if (response.isSuccessful){
                    Log.d("CLOSED", response.body()?.message.toString())
                }
            }
            catch (e: IOException){
                Log.e("ERROR", e.message.toString())
            }
            catch (_: Exception){
                Log.e("ERROR", "UNKNOWN ERROR")
            }
        }
        super.onCleared()
    }
    fun getUsers(){
        viewModelScope.launch {
            addChatUiState = try {
                val requestHeader = "Bearer " + flickzyState.value.onlineJWT
                val response = MessageApi.retrofitService.getUsers(
                    token = requestHeader
                )
                if (response.isSuccessful){
                    if (response.headers()["X-Refresh-Token"] != null){
                        _flickzyState.update {
                            it.copy(
                                onlineJWT = response.headers()["X-Refresh-Token"] ?: "NO OBTENIDO"
                            )
                        }
                    }
                    AddChatUiState.Success(response.body() ?: emptyList())
                }
                else{
                    val errorJson = response.errorBody()?.string()
                    val parsedError = try {
                        errorJson?.let {
                            json.decodeFromString<Error>(it)
                        }
                    } catch (_: Exception) {
                        Error(listOf("Not parseable Error"))
                    }
                    val errorMsg = parsedError?.errors?.joinToString() ?: "Error HTTP ${response.code()}"
                    AddChatUiState.Error(errorMsg)
                }
            } catch (e: IOException){
                AddChatUiState.Error(e.message.toString())
            }
            catch (_: Exception){
                AddChatUiState.Error("UNKNOWN ERROR")
            }
        }
    }

    private fun sha1(text: String) : String{
        val bytes = text.toByteArray()
        val md = MessageDigest.getInstance("SHA-1")
        val digest = md.digest(bytes)
        return digest.joinToString(""){
            "%02x".format(it)
        }
    }

    fun updateRegisterName(name: String){
        _registerFormUI.update {
            it.copy(
                name = name
            )
        }
    }

    fun updateRegisterMail(mail: String){
        _registerFormUI.update {
            it.copy(
                mail = mail
            )
        }
    }

    fun updateRegisterPassword(pass: String){
        _registerFormUI.update {
            it.copy(
                password = pass
            )
        }
    }

    private fun sendRegisterForm(
        name: String,
        mail: String,
        password: String
    ){
        val hashedPassword = sha1(password)
        val userToRegister = RegisterRequest(
            name = name.trim().lowercase(),
            mail = mail.trim().lowercase(),
            pass = hashedPassword
        )
        viewModelScope.launch {
            registerReqUiState = RegisterReqUiState.Loading
            registerReqUiState = try {
                MessageApi.retrofitService.register(userToRegister)
                RegisterReqUiState.Success("Usuario registrado correctamente")
            }
            catch (e: IOException){
                RegisterReqUiState.Error(e.message.toString())
            }
            catch (e: HttpException){
                val errorJson = e.response()?.errorBody()?.string()
                val parsedError = try {
                    errorJson?.let {
                        json.decodeFromString<RegisterResponse>(it)
                    }
                } catch (_: Exception) {
                    null
                }
                val errorMsg = parsedError?.errors?.joinToString() ?: "Error HTTP ${e.code()}"
                RegisterReqUiState.Error(errorMsg)
            }
            catch (_: Exception){
                RegisterReqUiState.Error("UNKNOWN ERROR")
            }
        }
    }

    fun onClickSendRegister(){
        sendRegisterForm(
            this.registerFormUI.value.name,
            this.registerFormUI.value.mail,
            this.registerFormUI.value.password
        )
    }

    fun onRegisterErrorCloseAlert(){
        this.registerReqUiState = RegisterReqUiState.NotSended
    }

    fun updateLoginMail(mail: String){
        _loginFormUI.update {
            it.copy(
                mail = mail
            )
        }
    }

    fun updateLoginPassword(pass: String){
        _loginFormUI.update {
            it.copy(
                pass = pass
            )
        }
    }

    private fun sendLoginForm(
        mail: String,
        password: String
    ){
        val hashedPassword = sha1(password)
        val userToLogin = LoginRequest(
            mail = mail.trim().lowercase(),
            pass = hashedPassword
        )
        viewModelScope.launch {
            loginReqUiState = try {
                val response = MessageApi.retrofitService.login(userToLogin)
                _flickzyState.update {
                    it.copy(
                        loggedUserId = response.id ?: -1,
                        loggedUserName = response.username ?: "",
                        loggedUserMail = response.mail ?: "",
                        onlineJWT = response.online_token ?: "NO OBTENIDO"
                    )
                }
                TokenStore.savePersistentToken(response.persistent_token ?: "")
                LoginReqUiState.Success("Sesión iniciada, dirigiendo a pantalla principal")
            } catch (e: IOException){
                LoginReqUiState.Error(e.message.toString())
            }
            catch (e: HttpException){
                val errorJson = e.response()?.errorBody()?.string()
                val parsedError = try {
                    errorJson?.let {
                        json.decodeFromString<LoginResponse>(it)
                    }
                } catch (_: Exception) {
                    null
                }
                val errorMsg = parsedError?.errors?.joinToString() ?: "Error HTTP ${e.code()}"
                LoginReqUiState.Error(errorMsg)
            }
            catch (_: Exception){
                LoginReqUiState.Error("UNKNOWN ERROR")
            }
        }
    }

    fun onClickSendLogin(){
        sendLoginForm(
            mail = this.loginFormUI.value.mail,
            password = this.loginFormUI.value.pass
        )
    }

    fun onLoginErrorCloseAlert(){
        this.loginReqUiState = LoginReqUiState.NotSended
    }

    fun getChats(){
        val requestBody = ChatRequest(flickzyState.value.loggedUserId)
        val requestHeader = "Bearer " + flickzyState.value.onlineJWT
        viewModelScope.launch {
            chatUiState = try {
                val response = MessageApi.retrofitService.getChats(
                    token = requestHeader,
                    request = requestBody
                )
                if(response.isSuccessful){
                    if (response.headers()["X-Refresh-Token"] != null){
                        _flickzyState.update {
                            it.copy(
                                onlineJWT = response.headers()["X-Refresh-Token"] ?: "NO OBTENIDO"
                            )
                        }
                    }
                    ChatUiState.Success(response.body() ?: emptyList())
                }
                else{
                    val errorJson = response.errorBody()?.string()
                    val parsedError = try {
                        errorJson?.let {
                            json.decodeFromString<Error>(it)
                        }
                    } catch (_: Exception) {
                        Error(listOf("Not parseable Error"))
                    }
                    val errorMsg = parsedError?.errors?.joinToString() ?: "Error HTTP ${response.code()}"
                    ChatUiState.Error(errorMsg)
                }
            }
            catch (e: IOException){
                ChatUiState.Error(e.message.toString())
            }
            catch (_: Exception){
                ChatUiState.Error("UNKNOWN ERROR")
            }
        }
    }

    fun newChat(
        destId: Int
    ){
        val requestBody = NewChatRequest(
            userId = flickzyState.value.loggedUserId,
            destId = destId
        )
        val requestHeader = "Bearer " + flickzyState.value.onlineJWT
        viewModelScope.launch {
            newChatReqUiState = try {
                val response = MessageApi.retrofitService.newChat(
                    token = requestHeader,
                    request = requestBody
                )
                if(response.isSuccessful){
                    if (response.headers()["X-Refresh-Token"] != null){
                        _flickzyState.update {
                            it.copy(
                                onlineJWT = response.headers()["X-Refresh-Token"] ?: "NO OBTENIDO"
                            )
                        }
                    }
                    NewChatReqUiState.Success(response.body() ?: Success("Completado, sin respuesta"))
                }
                else{
                    val errorJson = response.errorBody()?.string()
                    val parsedError = try {
                        errorJson?.let {
                            json.decodeFromString<Error>(it)
                        }
                    } catch (_: Exception) {
                        Error(listOf("Not parseable Error"))
                    }
                    val errorMsg = parsedError?.errors?.joinToString() ?: "Error HTTP ${response.code()}"
                    NewChatReqUiState.Error(errorMsg)
                }
            }
            catch (e: IOException){
                NewChatReqUiState.Error(e.message.toString())
            }
            catch (_: Exception){
                NewChatReqUiState.Error("UNKNOWN ERROR")
            }
        }
    }

    fun onNewChatErrorCloseAlert(){
        this.newChatReqUiState = NewChatReqUiState.NotSelected
    }

    fun onNewChatSuccessResetState(){
        this.newChatReqUiState = NewChatReqUiState.NotSelected
    }

    fun getMessages(
        chatId: Int
    ) {
        _flickzyState.update {
            it.copy(
                selectedChatId = chatId
            )
        }
        val requestBody = MessageRequest(
            idChat = chatId
        )
        val requestHeader = "Bearer " + flickzyState.value.onlineJWT
        viewModelScope.launch {
            messageReqUiState = try {
                val response = MessageApi.retrofitService.getMessages(
                    token = requestHeader,
                    request = requestBody
                )
                if(response.isSuccessful){
                    if (response.headers()["X-Refresh-Token"] != null){
                        _flickzyState.update {
                            it.copy(
                                onlineJWT = response.headers()["X-Refresh-Token"] ?: "NO OBTENIDO"
                            )
                        }
                    }
                    MessageReqUiState.Success(response.body() ?: emptyList())
                }
                else{
                    val errorJson = response.errorBody()?.string()
                    val parsedError = try {
                        errorJson?.let {
                            json.decodeFromString<Error>(it)
                        }
                    } catch (_: Exception) {
                        Error(listOf("Not parseable Error"))
                    }
                    val errorMsg = parsedError?.errors?.joinToString() ?: "Error HTTP ${response.code()}"
                    MessageReqUiState.Error(errorMsg)
                }
            }
            catch (e: IOException){
                MessageReqUiState.Error(e.message.toString())
            }
            catch (_: Exception){
                MessageReqUiState.Error("UNKNOWN ERROR")
            }
        }
    }

    fun updateMessageContent(
        content: String
    ){
        _messageFormUI.update {
            it.copy(
                content = content
            )
        }
    }

    fun sendMessage(){
        val requestBody = SendMessageRequest(
            chatId = flickzyState.value.selectedChatId,
            senderId = flickzyState.value.loggedUserId,
            content = messageFormUI.value.content
        )
        val requestHeader = "Bearer " + flickzyState.value.onlineJWT
        viewModelScope.launch {
            sendMessageUIState = try {
                val response = MessageApi.retrofitService.sendMessage(
                    token = requestHeader,
                    request = requestBody
                )
                if(response.isSuccessful){
                    if (response.headers()["X-Refresh-Token"] != null){
                        _flickzyState.update {
                            it.copy(
                                onlineJWT = response.headers()["X-Refresh-Token"] ?: "NO OBTENIDO"
                            )
                        }
                    }
                    _messageFormUI.update {
                        it.copy(
                            content = ""
                        )
                    }
                    SendMessageUIState.Success(response.body() ?: Success("Enviado"))

                }
                else{
                    val errorJson = response.errorBody()?.string()
                    val parsedError = try {
                        errorJson?.let {
                            json.decodeFromString<Error>(it)
                        }
                    } catch (_: Exception) {
                        Error(listOf("Not parseable Error"))
                    }
                    val errorMsg = parsedError?.errors?.joinToString() ?: "Error HTTP ${response.code()}"
                    SendMessageUIState.Error(errorMsg)
                }
            } catch (e: IOException){
                SendMessageUIState.Error(e.message.toString())
            } catch (_: Exception){
                SendMessageUIState.Error("UNKNOWN ERROR")
            } as SendMessageUIState
        }
    }

    fun getUserNameAndStatus(
        chatId: Int
    ){
        val requestBody = TopUser(
            myId = flickzyState.value.loggedUserId,
            chatId = chatId
        )
        viewModelScope.launch {
            topBarState = try {
                val response = MessageApi.retrofitService.getUser(requestBody)
                TopBarState.MessageScreen(response.otherUserName, response.onlineStatus)
            }
            catch (e: IOException){
                Log.e("ERROR", e.message.toString())
                TopBarState.MainScreen
            } catch (e: Exception){
                Log.e("ERROR", e.message.toString())
                TopBarState.MainScreen
            }
        }
    }

    fun resetTopBar(){
        topBarState = TopBarState.MainScreen
    }
}

