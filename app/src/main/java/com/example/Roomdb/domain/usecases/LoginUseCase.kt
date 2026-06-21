package com.example.Roomdb.domain.usecases

import com.example.Roomdb.data.model.UserProfile
import com.example.Roomdb.domain.repository.AuthRepository

class LoginUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<UserProfile> =
        authRepo.login(email, password)
}