package com.example.Roomdb.domain.usecases

import com.example.Roomdb.domain.repository.AuthRepository

class GetUserRoleUseCase(private val authRepo: AuthRepository) {
    suspend operator fun invoke(): String? = authRepo.getUserRole()
}