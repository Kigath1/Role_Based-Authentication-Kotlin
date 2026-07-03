package com.example.Roomdb

import android.app.Application
import androidx.room.Room
import com.example.Roomdb.api.AuthTokenHolder
import com.example.Roomdb.api.RetrofitInstance
import com.example.Roomdb.data.local.SecureTokenDataStore
import com.example.Roomdb.data.repositoryimpl.auth.AuthRepositoryImpl
import com.example.Roomdb.domain.repository.auth.AuthRepository
import com.example.Roomdb.domain.usecases.auth.CheckAuthStatusUseCase
import com.example.Roomdb.domain.usecases.auth.GetCurrentUserUseCase
import com.example.Roomdb.domain.usecases.auth.GetUserRoleUseCase
import com.example.Roomdb.domain.usecases.auth.LoginUseCase
import com.example.Roomdb.domain.usecases.auth.LogoutUseCase
import com.example.Roomdb.data.local.db.AppDatabase
import com.example.Roomdb.data.repository.WorkerRepositoryImpl
import com.example.Roomdb.data.repositoryimpl.auth.RegistrationRepositoryImpl
import com.example.Roomdb.data.repositoryimpl.employer.ClientJobRepositoryImpl
import com.example.Roomdb.data.repositoryimpl.employer.ClientProfileRepositoryImpl
import com.example.Roomdb.data.repositoryimpl.employer.MessageRepositoryImpl
import com.example.Roomdb.data.repositoryimpl.worker.WorkerJobRepositoryImpl
import com.example.Roomdb.data.repositoryimpl.worker.WorkerProfileRepositoryImpl
import com.example.Roomdb.domain.repository.auth.RegistrationRepository
import com.example.Roomdb.domain.repository.employer.ClientJobRepository
import com.example.Roomdb.domain.repository.employer.ClientProfileRepository
import com.example.Roomdb.domain.repository.employer.MessageRepository
import com.example.Roomdb.domain.repository.employer.WorkerRepository
import com.example.Roomdb.domain.repository.worker.WorkerJobRepository
import com.example.Roomdb.domain.repository.worker.WorkerProfileRepository
import com.example.Roomdb.domain.usecases.auth.RegisterUseCase
import com.example.Roomdb.domain.usecases.auth.ResendVerificationUseCase
import com.example.Roomdb.domain.usecases.auth.VerifyEmailUseCase
import com.example.Roomdb.domain.usecases.employer.AcceptCounterOfferUseCase
import com.example.Roomdb.domain.usecases.employer.CancelJobUseCase
import com.example.Roomdb.domain.usecases.employer.CheckClientProfileExistsUseCase
import com.example.Roomdb.domain.usecases.employer.CreateClientProfileUseCase
import com.example.Roomdb.domain.usecases.employer.CreateJobRequestUseCase
import com.example.Roomdb.domain.usecases.employer.GetClientJobsUseCase
import com.example.Roomdb.domain.usecases.employer.GetClientProfileUseCase
import com.example.Roomdb.domain.usecases.employer.GetConversationUseCase
import com.example.Roomdb.domain.usecases.employer.GetRecentConversationsUseCase
import com.example.Roomdb.domain.usecases.employer.GetWorkersUseCase
import com.example.Roomdb.domain.usecases.employer.SendMessageUseCase
import com.example.Roomdb.domain.usecases.employer.UpdateClientProfileUseCase
import com.example.Roomdb.domain.usecases.worker.AcceptJobUseCase
import com.example.Roomdb.domain.usecases.worker.CheckWorkerProfileExistsUseCase
import com.example.Roomdb.domain.usecases.worker.CompleteJobUseCase
import com.example.Roomdb.domain.usecases.worker.CounterOfferUseCase
import com.example.Roomdb.domain.usecases.worker.CreateWorkerProfileUseCase
import com.example.Roomdb.domain.usecases.worker.GetWorkerJobsUseCase
import com.example.Roomdb.domain.usecases.worker.GetWorkerProfileUseCase
import com.example.Roomdb.domain.usecases.worker.RejectJobUseCase
import com.example.Roomdb.domain.usecases.worker.StartJobUseCase
import com.example.Roomdb.domain.usecases.worker.UpdateWorkerProfileUseCase
import com.example.Roomdb.domain.usecases.worker.UploadDocumentUseCase
import com.example.Roomdb.viewmodel.worker.WorkerDashboardViewModel
import kotlin.jvm.java

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


    lateinit var registerUseCase: RegisterUseCase
    lateinit var verifyEmailUseCase: VerifyEmailUseCase
    lateinit var resendVerificationUseCase: ResendVerificationUseCase

    lateinit var createClientProfileUseCase: CreateClientProfileUseCase
    lateinit var getClientProfileUseCase: GetClientProfileUseCase
    lateinit var updateClientProfileUseCase: UpdateClientProfileUseCase

    lateinit var createWorkerProfileUseCase: CreateWorkerProfileUseCase
    lateinit var updateWorkerProfileUseCase: UpdateWorkerProfileUseCase

    lateinit var uploadDocumentUseCase: UploadDocumentUseCase
    lateinit var checkWorkerProfileExistsUseCase: CheckWorkerProfileExistsUseCase
    lateinit var checkClientProfileExistsUseCase: CheckClientProfileExistsUseCase
    lateinit var getWorkerProfileUseCase: GetWorkerProfileUseCase


    lateinit var clientJobRepository: ClientJobRepository
    lateinit var workerJobRepository: WorkerJobRepository

    lateinit var createJobRequestUseCase: CreateJobRequestUseCase
    lateinit var acceptCounterOfferUseCase: AcceptCounterOfferUseCase
    lateinit var cancelJobUseCase: CancelJobUseCase
    lateinit var getClientJobsUseCase: GetClientJobsUseCase

    lateinit var acceptJobUseCase: AcceptJobUseCase
    lateinit var rejectJobUseCase: RejectJobUseCase
    lateinit var counterOfferUseCase: CounterOfferUseCase
    lateinit var startJobUseCase: StartJobUseCase
    lateinit var completeJobUseCase: CompleteJobUseCase
    lateinit var getWorkerJobsUseCase: GetWorkerJobsUseCase

    override fun onCreate() {
        super.onCreate()
        try {

            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java,
                AppDatabase.NAME
            ).fallbackToDestructiveMigration().build()

            val userDao = db.userDao()
            val workerDao = db.workerDao()

            secureStore = SecureTokenDataStore(applicationContext)

            // ── Synchronous token injection ───────────────────────────────────
            kotlinx.coroutines.runBlocking {
                val token = secureStore.getAccessTokenOnce()
                if (token != null) {
                    AuthTokenHolder.token = token
                    android.util.Log.d("AppInit", "Token loaded: ${token.take(20)}...")
                } else {
                    android.util.Log.w("AppInit", "No token found — user not logged in")
                }
            }

            val authApi = RetrofitInstance.authApi
            val workerApi = RetrofitInstance.workerApi
            val messageApi = RetrofitInstance.messageApi

            // Auth
            authRepository = AuthRepositoryImpl(authApi, userDao, secureStore)
            loginUseCase = LoginUseCase(authRepository)
            getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
            getUserRoleUseCase = GetUserRoleUseCase(authRepository)
            logoutUseCase = LogoutUseCase(authRepository)
            checkAuthStatusUseCase = CheckAuthStatusUseCase(authRepository)

            // Workers
            workerRepository = WorkerRepositoryImpl(workerApi, workerDao)
            getWorkersUseCase = GetWorkersUseCase(workerRepository)

            // Messaging
            messageRepository = MessageRepositoryImpl(messageApi)
            getRecentConversationsUseCase = GetRecentConversationsUseCase(messageRepository)
            getConversationUseCase = GetConversationUseCase(messageRepository)
            sendMessageUseCase = SendMessageUseCase(messageRepository)

            // Registration
            val registrationRepository: RegistrationRepository =
                RegistrationRepositoryImpl(RetrofitInstance.authApi)
            registerUseCase = RegisterUseCase(registrationRepository)
            verifyEmailUseCase = VerifyEmailUseCase(registrationRepository)
            resendVerificationUseCase = ResendVerificationUseCase(registrationRepository)

            // Client profile
            val clientProfileRepository: ClientProfileRepository =
                ClientProfileRepositoryImpl(RetrofitInstance.clientProfileApi)
            createClientProfileUseCase = CreateClientProfileUseCase(clientProfileRepository)
            getClientProfileUseCase = GetClientProfileUseCase(clientProfileRepository)
            updateClientProfileUseCase = UpdateClientProfileUseCase(clientProfileRepository)

            // Worker profile
            val workerProfileRepository: WorkerProfileRepository =
                WorkerProfileRepositoryImpl(RetrofitInstance.workerProfileApi)
            createWorkerProfileUseCase = CreateWorkerProfileUseCase(workerProfileRepository)
            updateWorkerProfileUseCase = UpdateWorkerProfileUseCase(workerProfileRepository)
            uploadDocumentUseCase = UploadDocumentUseCase(workerProfileRepository)
            checkWorkerProfileExistsUseCase = CheckWorkerProfileExistsUseCase(workerProfileRepository)
            checkClientProfileExistsUseCase = CheckClientProfileExistsUseCase(clientProfileRepository)
            getWorkerProfileUseCase  = GetWorkerProfileUseCase(workerProfileRepository)

            clientJobRepository = ClientJobRepositoryImpl(RetrofitInstance.clientJobApi)
            createJobRequestUseCase = CreateJobRequestUseCase(clientJobRepository)
            acceptCounterOfferUseCase = AcceptCounterOfferUseCase(clientJobRepository)
            cancelJobUseCase = CancelJobUseCase(clientJobRepository)
            getClientJobsUseCase = GetClientJobsUseCase(clientJobRepository)

            workerJobRepository = WorkerJobRepositoryImpl(RetrofitInstance.workerJobApi)
            acceptJobUseCase = AcceptJobUseCase(workerJobRepository)
            rejectJobUseCase = RejectJobUseCase(workerJobRepository)
            counterOfferUseCase = CounterOfferUseCase(workerJobRepository)
            startJobUseCase = StartJobUseCase(workerJobRepository)
            completeJobUseCase = CompleteJobUseCase(workerJobRepository)
            getWorkerJobsUseCase = GetWorkerJobsUseCase(workerJobRepository)

            android.util.Log.d("AppInit", "All use cases initialized successfully")

        } catch (e: Exception) {
            android.util.Log.e("AppInit", "FATAL: Application init failed", e)
            throw e   // re-throw so the crash is visible with the real cause
        }
    }

}

