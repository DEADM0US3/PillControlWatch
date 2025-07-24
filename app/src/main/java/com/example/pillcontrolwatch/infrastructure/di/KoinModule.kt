package com.example.pillcontrolwatch.infrastructure.di

import com.example.pillcontrolwatch.aplication.`view-model`.FriendsViewModel
import com.example.pillcontrolwatch.domain.use_case.GetUserProfile
import com.example.pillcontrolwatch.domain.use_case.UpdateUserProfile
import com.example.pillcontrolwatch.domain.use_case.ValidateAge
import com.example.pillcontrolwatch.domain.use_case.ValidateEmail
import com.example.pillcontrolwatch.domain.use_case.ValidateName
import com.example.pillcontrolwatch.domain.use_case.ValidatePassword
import com.example.pillcontrolwatch.domain.use_case.ValidatePhone
import com.example.pillcontrolwatch.domain.use_case.ValidateTerms
import com.example.pills.pills.domain.repository.CycleRepository
import com.example.pills.pills.domain.repository.FriendRepository
import com.example.pills.pills.domain.repository.LoginRepository
import com.example.pills.pills.domain.repository.NotificationRepository
import com.example.pills.pills.domain.repository.PillRepository
import com.example.pills.pills.domain.repository.ProfileRepository
import com.example.pills.pills.domain.repository.ResetPasswordRepository
import com.example.pills.pills.domain.repository.SetPasswordRepository
import com.example.pills.pills.domain.repository.SignUpRepository
import com.example.pills.pills.domain.supabase.SupabaseClientProvider
import com.example.pills.pills.infrastructure.ViewModel.MainViewModel
import com.example.pills.pills.infrastructure.ViewModel.PillViewModel
import com.example.pills.pills.presentation.cycle.CycleViewModel
import com.example.pills.pills.presentation.homePage.HomeViewModel
import com.example.pills.pills.presentation.login.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


/**
 * - This module is used to define dependencies that are instances of normal classes
 * - Register all dependencies your ViewModels need.
 */
val appModule = module {
    // Register SupabaseClientProvider as a Singleton
    single { SupabaseClientProvider.client }

    // Define a singleton for SignUpRepository
    single { SignUpRepository(get()) }

    single { ResetPasswordRepository(get()) }

    single { SetPasswordRepository(get()) }

    single { ProfileRepository(get(), androidContext()) }

    single { FriendRepository(get()) }

    single { NotificationRepository(get()) }

    single { PillRepository(get()) }

    single { CycleRepository(get()) }

    // Provide LoginRepository (Now with SupabaseClientProvider)
    single { LoginRepository(androidContext(), get() ) }

    factoryOf(::ValidateEmail)
    factoryOf(::ValidatePassword)
    factoryOf(::ValidateTerms)
    factoryOf(::ValidateName)
    factoryOf(::GetUserProfile)
    factoryOf(::UpdateUserProfile)
//    factoryOf(::UpdateProfileImage)
//    factoryOf(::UploadProfileImage)
//    factoryOf(::DeleteProfileImage)
    factoryOf(::ValidatePhone)
    factoryOf(::ValidateAge)
}


/**
 * - This module provides all ViewModels used in the app.
 * - Every ViewModel must be declared here.
 */
val viewModelModule = module {

    viewModelOf(::LoginViewModel) // Added LoginViewModel
    viewModelOf(::HomeViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::FriendsViewModel)
    viewModelOf(::CycleViewModel)
    viewModelOf(::PillViewModel)

}