package com.example.Roomdb

import android.app.Application
import androidx.room.Room
import com.example.Roomdb.api.RetrofitInstance
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.repositoryimpl.AuthRepositoryImpl
import com.example.Roomdb.domain.repository.auth.AuthRepository
import com.example.Roomdb.domain.usecases.auth.CheckAuthStatusUseCase
import com.example.Roomdb.domain.usecases.auth.GetCurrentUserUseCase
import com.example.Roomdb.domain.usecases.auth.GetUserRoleUseCase
import com.example.Roomdb.domain.usecases.auth.LoginUseCase
import com.example.Roomdb.domain.usecases.auth.LogoutUseCase
import com.example.Roomdb.viewmodel.auth.AuthViewModel
import com.example.Roomdb.data.local.db.AppDatabase
import com.example.Roomdb.data.repository.WorkerRepositoryImpl
import com.example.Roomdb.domain.repository.employer.WorkerRepository
import com.example.Roomdb.domain.usecases.employer.GetWorkersUseCase
import com.example.Roomdb.viewmodel.employer.ClientHomeViewModel
import kotlin.jvm.java

class TestKonnectApplication : Application() {

    // Expose repositories and use cases for the Activity to wire into ViewModelFactory
    lateinit var authRepository: AuthRepository
    lateinit var workerRepository: WorkerRepository
    lateinit var getWorkersUseCase: GetWorkersUseCase
    lateinit var loginUseCase: LoginUseCase
    lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    lateinit var getUserRoleUseCase: GetUserRoleUseCase
    lateinit var logoutUseCase: LogoutUseCase
    lateinit var checkAuthStatusUseCase: CheckAuthStatusUseCase

    override fun onCreate() {
        super.onCreate()

        // Database — fallbackToDestructiveMigration for dev; replace with real migrations before release
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            AppDatabase.NAME
        )
            .fallbackToDestructiveMigration()
            .build()

        val userDao = db.userDao()
        val workerDao = db.workerDao()

        val secureStore = SecureTokenDataStore(applicationContext)
        val authApi = RetrofitInstance.authApi
        val workerApi = RetrofitInstance.workerApi

        authRepository = AuthRepositoryImpl(authApi, userDao, secureStore)
        loginUseCase = LoginUseCase(authRepository)
        getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
        getUserRoleUseCase = GetUserRoleUseCase(authRepository)
        logoutUseCase = LogoutUseCase(authRepository)
        checkAuthStatusUseCase = CheckAuthStatusUseCase(authRepository)

        workerRepository = WorkerRepositoryImpl(workerApi, workerDao)
        getWorkersUseCase = GetWorkersUseCase(workerRepository)
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