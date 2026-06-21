package com.example.Roomdb.domain.usecases

import com.example.Roomdb.domain.repository.AuthRepository

class LogoutUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke() = authRepo.logout()
}