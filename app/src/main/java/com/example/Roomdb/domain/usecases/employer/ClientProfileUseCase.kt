package com.example.Roomdb.domain.usecases.employer

import com.example.Roomdb.domain.repository.employer.ClientProfileRepository

class CreateClientProfileUseCase(private val repo: ClientProfileRepository) {
    suspend operator fun invoke(
        email: String, fullName: String, phoneNumber: String, location: String
    ) = repo.createProfile(email, fullName, phoneNumber, location)
}