package com.example.Roomdb.domain.repository.worker

import com.example.Roomdb.data.remote.model.worker.WalletBalanceResponse
import com.example.Roomdb.data.remote.model.worker.WalletTransactionDto
import com.example.Roomdb.data.remote.model.worker.WithdrawResponse

interface WalletRepository {
    suspend fun getBalance(): Result<WalletBalanceResponse>
    suspend fun withdraw(amount: Double, phoneNumber: String): Result<WithdrawResponse>
    suspend fun getTransactions(): Result<List<WalletTransactionDto>>
}