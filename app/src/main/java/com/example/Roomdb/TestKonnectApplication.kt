package com.example.Roomdb

import android.app.Application
import androidx.room.Room
import com.example.Roomdb.api.AuthTokenHolder
import com.example.Roomdb.api.RetrofitInstance
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.repositoryimpl.AuthRepositoryImpl
import com.example.Roomdb.domain.repository.auth.AuthRepository
import com.example.Roomdb.domain.usecases.auth.CheckAuthStatusUseCase
import com.example.Roomdb.domain.usecases.auth.GetCurrentUserUseCase
import com.example.Roomdb.domain.usecases.auth.GetUserRoleUseCase
import com.example.Roomdb.domain.usecases.auth.LoginUseCase
import com.example.Roomdb.domain.usecases.auth.LogoutUseCase
import com.example.Roomdb.data.local.db.AppDatabase
import com.example.Roomdb.data.repository.WorkerRepositoryImpl
import com.example.Roomdb.data.repositoryimpl.employer.MessageRepositoryImpl
import com.example.Roomdb.domain.repository.employer.MessageRepository
import com.example.Roomdb.domain.repository.employer.WorkerRepository
import com.example.Roomdb.domain.usecases.employer.GetConversationUseCase
import com.example.Roomdb.domain.usecases.employer.GetRecentConversationsUseCase
import com.example.Roomdb.domain.usecases.employer.GetWorkersUseCase
import com.example.Roomdb.domain.usecases.employer.SendMessageUseCase
import kotlin.jvm.java
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestKonnectApplication : Application() {

    lateinit var authRepository: AuthRepository
    lateinit var workerRepository: WorkerRepository
    lateinit var messageRepository: MessageRepository
    lateinit var secureStore: SecureTokenDataStore

    lateinit var loginUseCase: LoginUseCase
    lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    lateinit var getUserRoleUseCase: GetUserRoleUseCase
    lateinit var logoutUseCase: LogoutUseCase
    lateinit var checkAuthStatusUseCase: CheckAuthStatusUseCase
    lateinit var getWorkersUseCase: GetWorkersUseCase
    lateinit var getRecentConversationsUseCase: GetRecentConversationsUseCase
    lateinit var getConversationUseCase: GetConversationUseCase
    lateinit var sendMessageUseCase: SendMessageUseCase

    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.NAME
        ).fallbackToDestructiveMigration().build()

        val userDao = db.userDao()
        val workerDao = db.workerDao()

        // Assign to the lateinit var, not a local variable
        secureStore = SecureTokenDataStore(applicationContext)

        // Inject token into auth interceptor for returning users
        CoroutineScope(Dispatchers.IO).launch {
            val token = secureStore.getAccessTokenOnce()
            if (token != null) {
                AuthTokenHolder.token = token
                android.util.Log.d("AppInit", "Token loaded: ${token.take(20)}...")
            } else {
                android.util.Log.w("AppInit", "No token found in store — user not logged in")
            }
        }

        val authApi    = RetrofitInstance.authApi
        val workerApi  = RetrofitInstance.workerApi
        val messageApi = RetrofitInstance.messageApi

        // Auth
        authRepository         = AuthRepositoryImpl(authApi, userDao, secureStore)
        loginUseCase           = LoginUseCase(authRepository)
        getCurrentUserUseCase  = GetCurrentUserUseCase(authRepository)
        getUserRoleUseCase     = GetUserRoleUseCase(authRepository)
        logoutUseCase          = LogoutUseCase(authRepository)
        checkAuthStatusUseCase = CheckAuthStatusUseCase(authRepository)

        // Workers
        workerRepository  = WorkerRepositoryImpl(workerApi, workerDao)
        getWorkersUseCase = GetWorkersUseCase(workerRepository)

        // Messaging
        messageRepository             = MessageRepositoryImpl(messageApi)
        getRecentConversationsUseCase = GetRecentConversationsUseCase(messageRepository)
        getConversationUseCase        = GetConversationUseCase(messageRepository)
        sendMessageUseCase            = SendMessageUseCase(messageRepository)
    }
}



//import android.app.Application
//import androidx.room.Room
//import com.example.Roomdb.api.RetrofitInstance
//import com.example.Roomdb.data.local.SecureTokenDataStore
//import com.example.Roomdb.data.repositoryimpl.AuthRepositoryImpl
//import com.example.Roomdb.domain.repository.auth.AuthRepository
//import com.example.Roomdb.domain.usecases.auth.CheckAuthStatusUseCase
//import com.example.Roomdb.domain.usecases.auth.GetCurrentUserUseCase
//import com.example.Roomdb.domain.usecases.auth.GetUserRoleUseCase
//import com.example.Roomdb.domain.usecases.auth.LoginUseCase
//import com.example.Roomdb.domain.usecases.auth.LogoutUseCase
//import com.example.Roomdb.viewmodel.auth.AuthViewModel
//import com.example.Roomdb.data.local.db.AppDatabase
//import kotlin.jvm.java
//
//class TestKonnectApplication : Application() {
//    lateinit var authViewModel: AuthViewModel
//
//    override fun onCreate() {
//        super.onCreate()
//
//        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "testkonnect_db")
//            .build()
//        val userDao = db.userDao()
//
//        val secureStore = SecureTokenDataStore(applicationContext)
//        val authApi = RetrofitInstance.authApi
//
//        val authRepository: AuthRepository = AuthRepositoryImpl(authApi, userDao, secureStore)
//
//        authViewModel = AuthViewModel(
//            loginUseCase = LoginUseCase(authRepository),
//            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
//            getUserRoleUseCase = GetUserRoleUseCase(authRepository),
//            logoutUseCase = LogoutUseCase(authRepository),
//            checkAuthStatusUseCase = CheckAuthStatusUseCase(authRepository)
//        )
//    }
//}