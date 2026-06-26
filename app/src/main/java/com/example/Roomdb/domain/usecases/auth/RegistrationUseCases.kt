package com.example.Roomdb.domain.usecases.auth

import com.example.Roomdb.domain.repository.auth.RegistrationRepository


class RegisterUseCase(private val repo: RegistrationRepository) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        firstName: String,
        secondName: String,
        role: String
    ) = repo.register(username, email, password, firstName, secondName, role)
}

class VerifyEmailUseCase(private val repo: RegistrationRepository) {
    suspend operator fun invoke(token: String, email: String?) =
        repo.verifyEmail(token, email)
}

class ResendVerificationUseCase(private val repo: RegistrationRepository) {
    suspend operator fun invoke(email: String) = repo.resendVerification(email)
}