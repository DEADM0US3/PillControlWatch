package com.example.pillcontrolwatch.domain.use_case

import com.example.pills.pills.domain.repository.ProfileRepository

/**
 * Use case que actualiza únicamente el nombre completo y el correo electrónico del usuario.
 */
class UpdateUserProfile(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String
    ): Result<Unit> {
        if (fullName.isBlank() || email.isBlank()) {
            return Result.failure(Exception("El nombre y correo no pueden estar vacíos"))
        }

        return repository.updateUserProfile(
            fullName = fullName,
            email = email,
        )
    }
}
