package com.example.Roomdb.domain.usecases

import com.example.Roomdb.data.model.UserProfile
import com.example.Roomdb.domain.repository.AuthRepository

class GetCurrentUserUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke(): UserProfile? = authRepo.getCurrentUser()
}