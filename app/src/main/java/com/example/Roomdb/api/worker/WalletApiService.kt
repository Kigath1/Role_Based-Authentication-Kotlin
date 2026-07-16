package com.example.Roomdb.api.worker

import com.example.Roomdb.data.remote.model.worker.WalletBalanceResponse
import com.example.Roomdb.data.remote.model.worker.WalletTransactionDto
import com.example.Roomdb.data.remote.model.worker.WithdrawRequest
import com.example.Roomdb.data.remote.model.worker.WithdrawResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WalletApiService {

    @GET("wallet/balance")
    suspend fun getWalletBalance(): WalletBalanceResponse

    @POST("wallet/withdraw")
    suspend fun withdraw(@Body request: WithdrawRequest): WithdrawResponse

    @GET("wallet/transactions")
    suspend fun getTransactions(): List<WalletTransactionDto>
}