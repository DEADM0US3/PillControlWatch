package com.example.pillcontrolwatch.domain.use_case

import com.example.pills.pills.domain.repository.ProfileRepository
import com.example.pills.pills.domain.repository.ProfileRepository.UserProfile

class GetUserProfile(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(): Result<UserProfile> {
        return repository.getUserProfile()
    }
}
