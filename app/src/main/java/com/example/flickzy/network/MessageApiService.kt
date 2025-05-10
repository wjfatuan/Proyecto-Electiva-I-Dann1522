package com.example.flickzy.network
import com.example.flickzy.data.Chat
import com.example.flickzy.data.ChatRequest
import com.example.flickzy.data.LoginRequest
import com.example.flickzy.data.LoginResponse
import com.example.flickzy.data.Message
import com.example.flickzy.data.MessageRequest
import com.example.flickzy.data.NewChatRequest
import com.example.flickzy.data.RegisterRequest
import com.example.flickzy.data.RegisterResponse
import com.example.flickzy.data.SendMessageRequest
import com.example.flickzy.data.Success
import com.example.flickzy.data.TopUser
import com.example.flickzy.data.TopUserResponse
import com.example.flickzy.data.User
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

private const val BASE_URL = "http://192.168.1.6:5000"
private val json = Json {ignoreUnknownKeys = true}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface MessageApiService{
    @POST("user")
    suspend fun getUser(
        @Body request: TopUser
    ) : TopUserResponse
    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") token: String
    ) : Response<List<User>>
    @POST("chats")
    suspend fun getChats(
        @Header("Authorization") token: String,
        @Body request: ChatRequest
    ) : Response<List<Chat>>
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse
    @POST("login")
    suspend fun login(@Body request: LoginRequest) : LoginResponse
    @POST("new_chat")
    suspend fun newChat(
        @Header("Authorization") token: String,
        @Body request: NewChatRequest
    ) : Response<Success>
    @POST("messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Body request: MessageRequest
    ): Response<List<Message>>
    @POST("send_message")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body request: SendMessageRequest
    ): Response<Success>
    @GET("logout")
    suspend fun logout(
        @Header("Authorization") token: String,
    ): Response<Success>
}

object MessageApi{
    val retrofitService : MessageApiService by lazy {
        retrofit.create(MessageApiService::class.java)
    }
}


