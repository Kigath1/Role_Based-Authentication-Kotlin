package com.example.Roomdb.domain.usecases.auth

import com.example.Roomdb.domain.repository.auth.AuthRepository

class GetUserRoleUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke(): String? = authRepo.getUserRole()
}