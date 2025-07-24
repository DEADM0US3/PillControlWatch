package com.example.pillcontrolwatch

import android.app.Application
import com.example.pillcontrolwatch.infrastructure.di.appModule
import com.example.pillcontrolwatch.infrastructure.di.viewModelModule
import com.example.pills.pills.domain.supabase.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.Default).launch {
            SupabaseClientProvider.client.auth.loadFromStorage()
        }

        startKoin {
            androidLogger()  // Logs Koin initialization
            androidContext(this@App)
            modules(appModule, viewModelModule) // Add your modules here created in KoinModule.kt
        }
    }

}