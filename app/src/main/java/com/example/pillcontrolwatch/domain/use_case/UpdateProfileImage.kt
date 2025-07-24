//package com.example.pills.pills.domain.use_case
//
//import com.example.pills.pills.domain.repository.ProfileRepository
//
///**
// * Use case for updating user's profile image
// */
//class UpdateProfileImage(
//    private val repository: ProfileRepository
//) {
//    suspend operator fun invoke(imageUrl: String): Result<Unit> {
//        return repository.updateProfileImage(imageUrl)
//    }
//}