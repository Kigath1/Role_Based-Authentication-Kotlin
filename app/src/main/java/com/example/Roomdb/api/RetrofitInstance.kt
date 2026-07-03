package com.example.Roomdb.api


import com.example.Roomdb.api.auth.AuthApiService
import com.example.Roomdb.api.employer.ClientJobApiService
import com.example.Roomdb.api.employer.ClientProfileApiService
import com.example.Roomdb.api.employer.MessageApiService
import com.example.Roomdb.api.employer.WorkerApiService
import com.example.Roomdb.api.worker.WorkerJobApiService
import com.example.Roomdb.api.worker.WorkerProfileApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlin.getValue


private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}

private const val BASE_URL = "https://client-search-backend.onrender.com/api/"

// Call this once from Application to inject the token
object AuthTokenHolder {
    var token: String = ""
}

private fun buildClient(requiresAuth: Boolean): OkHttpClient {
    val builder = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)

    if (requiresAuth) {
        builder.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${AuthTokenHolder.token}")
                .build()
            chain.proceed(request)
        }
    }

    return builder.build()
}

private fun buildRetrofit(requiresAuth: Boolean): Retrofit =
    Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(buildClient(requiresAuth))
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

object RetrofitInstance {
    val authApi: AuthApiService by lazy {
        buildRetrofit(requiresAuth = false).create(AuthApiService::class.java)
    }

    val workerApi: WorkerApiService by lazy {
        buildRetrofit(requiresAuth = false).create(WorkerApiService::class.java)
    }

    val messageApi: MessageApiService by lazy {
        buildRetrofit(requiresAuth = true).create(MessageApiService::class.java)
    }

    val clientProfileApi: ClientProfileApiService by lazy {
        buildRetrofit(requiresAuth = true).create(ClientProfileApiService::class.java)
    }
    val workerProfileApi: WorkerProfileApiService by lazy {
        buildRetrofit(requiresAuth = true).create(WorkerProfileApiService::class.java)
    }
    val clientJobApi: ClientJobApiService by lazy {
        buildRetrofit(requiresAuth = true).create(ClientJobApiService::class.java)
    }

    val workerJobApi: WorkerJobApiService by lazy {
        buildRetrofit(requiresAuth = true).create(WorkerJobApiService::class.java)
    }
}
