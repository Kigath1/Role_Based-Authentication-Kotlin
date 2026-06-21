package com.example.Roomdb.api


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import kotlin.getValue
import kotlin.jvm.java

private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

object RetrofitInstance {
    val authApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://client-search-backend.onrender.com/api/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuthApiService::class.java)

    }
}


private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    encodeDefaults = true
}
