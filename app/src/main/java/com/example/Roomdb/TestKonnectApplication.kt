package com.example.Roomdb

import android.app.Application
import androidx.room.Room
import com.example.Roomdb.api.RetrofitInstance
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.repositoryimpl.AuthRepositoryImpl
import com.example.Roomdb.domain.repository.AuthRepository
import com.example.Roomdb.domain.usecases.CheckAuthStatusUseCase
import com.example.Roomdb.domain.usecases.GetCurrentUserUseCase
import com.example.Roomdb.domain.usecases.GetUserRoleUseCase
import com.example.Roomdb.domain.usecases.LoginUseCase
import com.example.Roomdb.domain.usecases.LogoutUseCase
import com.example.Roomdb.viewmodel.AuthViewModel
import com.example.Roomdb.data.local.AppDatabase
import kotlin.jvm.java

class TestKonnectApplication : Application() {
    lateinit var authViewModel: AuthViewModel

    override fun onCreate() {
        super.onCreate()

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "testkonnect_db")
            .build()
        val userDao = db.userDao()

        val secureStore = SecureTokenDataStore(applicationContext)
        val authApi = RetrofitInstance.authApi

        val authRepository: AuthRepository = AuthRepositoryImpl(authApi, userDao, secureStore)

        authViewModel = AuthViewModel(
            loginUseCase = LoginUseCase(authRepository),
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository),
            getUserRoleUseCase = GetUserRoleUseCase(authRepository),
            logoutUseCase = LogoutUseCase(authRepository),
            checkAuthStatusUseCase = CheckAuthStatusUseCase(authRepository)
        )
    }
}