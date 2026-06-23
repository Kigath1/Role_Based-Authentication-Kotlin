package com.example.Roomdb.domain.usecases.auth

import com.example.Roomdb.domain.repository.auth.AuthRepository

class CheckAuthStatusUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke(): Boolean = authRepo.isLoggedIn()
}