package com.example.pills.pills.presentation.login

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navigateToSignUp: () -> Unit,
    navigateToForgetPassword: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToOtp: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val viewModel: LoginViewModel = koinViewModel()
    val state = viewModel.state
    val context = LocalContext.current
    val activity = LocalActivity.current ?: throw IllegalStateException("LoginScreen must be hosted in an Activity")

    LaunchedEffect(key1 = context) {
        viewModel.validationEvents.collect { event ->
            when (event) {
                is LoginViewModel.ValidationEvent.Success -> {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
                is LoginViewModel.ValidationEvent.EmailNotVerified -> {
                    Toast.makeText(context, "Email not verified. OTP sent.", Toast.LENGTH_LONG).show()
                    navigateToOtp(event.email)
                }
                is LoginViewModel.ValidationEvent.Failure -> {
                    Log.e("LoginUI.kt", "Error: ${event.error}")
                    Toast.makeText(context, "Error: ${event.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFD56A83))
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {


        Spacer(modifier = Modifier.height(0.dp))

        Text(
            text = "Iniciar Sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(LoginFormEvent.EmailChanged(it)) },
            isError = state.emailError != null,
            shape = CircleShape,
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                cursorColor = Color(0xFFDF7A92),
                focusedIndicatorColor = Color(0xFFDF7A92),
                unfocusedIndicatorColor = Color(0xFFDF7A92),
                focusedLabelColor = Color(0xFFF6F0F1),
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,      // Color del texto cuando el campo está enfocado
                unfocusedTextColor = Color.White     // Color del texto cuando el campo no está enfocado
            )
        )
        if (state.emailError != null) {
            Text(
                text = state.emailError,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 22.dp),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(LoginFormEvent.PasswordChanged(it)) },
            isError = state.passwordError != null,
            shape = CircleShape,
            label = { Text("Contraseña") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                cursorColor = Color(0xFFDF7A92),
                focusedIndicatorColor = Color(0xFFDF7A92),
                unfocusedIndicatorColor = Color(0xFFDF7A92),
                focusedLabelColor = Color(0xFFF6F0F1),
                unfocusedLabelColor = Color.White,
                focusedTextColor = Color.White,      // Color del texto cuando el campo está enfocado
                unfocusedTextColor = Color.White     // Color del texto cuando el campo no está enfocado
            )
        )
        if (state.passwordError != null) {
            Text(
                text = state.passwordError,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 22.dp),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.onEvent(LoginFormEvent.Submit) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF36F9D))
        ) {
            Text(text = "Entrar", color = Color.White, fontWeight = FontWeight.Bold)
            if (state.isLoading) {
                Spacer(modifier = Modifier.width(10.dp))
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        /*Text(
            text = "¿Olvidaste tu contraseña?",
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.clickable { navigateToForgetPassword() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navigateToSignUp() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAFAFA))
        ) {
            Text("¿No tienes cuenta? Regístrate Aquí", color = Color(0xFFD56A83), fontWeight = FontWeight.Bold)
        }*/

        // O si prefieres un texto clickeable en lugar de botón:
        /*Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Continuar sin cuenta",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { navigateToHome() }
        )*/
    }
}
