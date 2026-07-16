package com.example.Roomdb.data.repositoryimpl.worker

import com.example.Roomdb.api.worker.WalletApiService
import com.example.Roomdb.data.remote.model.worker.WalletBalanceResponse
import com.example.Roomdb.data.remote.model.worker.WalletTransactionDto
import com.example.Roomdb.data.remote.model.worker.WithdrawRequest
import com.example.Roomdb.data.remote.model.worker.WithdrawResponse
import com.example.Roomdb.domain.repository.worker.WalletRepository
import retrofit2.HttpException
import java.io.IOException

class WalletRepositoryImpl(
    private val api: WalletApiService
) : WalletRepository {

    override suspend fun getBalance(): Result<WalletBalanceResponse> = runCatching {
        api.getWalletBalance()
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun withdraw(amount: Double, phoneNumber: String): Result<WithdrawResponse> = runCatching {
        api.withdraw(WithdrawRequest(amount = amount, destinationPhoneNumber = phoneNumber))
    }.recoverCatching { throw Exception(it.message) }

    override suspend fun getTransactions(): Result<List<WalletTransactionDto>> = runCatching {
        api.getTransactions()
    }.recoverCatching { throw Exception(it.message) }

    private fun handleException(e: Throwable): Result<Nothing> {
        return when (e) {
            is HttpException -> {
                val errorMessage = when (e.code()) {
                    400 -> "Invalid request"
                    401 -> "Please login again"
                    403 -> "Insufficient balance or unauthorized"
                    404 -> "Wallet not found"
                    429 -> "Too many requests, please try again later"
                    500 -> "Server error, please try again"
                    else -> "Error: ${e.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
            is IOException -> {
                Result.failure(Exception("Network error, please check your connection"))
            }
            else -> {
                Result.failure(Exception(e.message ?: "Unexpected error occurred"))
            }
        }
    }
}