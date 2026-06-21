package com.example.Roomdb.domain.usecases

import com.example.Roomdb.domain.repository.AuthRepository

class CheckAuthStatusUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke(): Boolean = authRepo.isLoggedIn()
}