package com.example.pillcontrolwatch.domain.use_case

class ValidatePhone {
    fun execute(phone: String): ValidationResult {
        // Trim the phone number to handle any leading or trailing spaces
        val trimmedPhone = phone.trim()

        // Check if phone is blank (optional field)
        if (trimmedPhone.isBlank()) {
            return ValidationResult(successful = true) // Phone is optional
        }

        // Check for minimum length (at least 7 digits for a valid phone number)
        if (trimmedPhone.length < 7) {
            return ValidationResult(
                successful = false,
                errorMessage = "El número de teléfono debe tener al menos 7 dígitos"
            )
        }

        // Check for maximum length (reasonable limit for phone numbers)
        if (trimmedPhone.length > 15) {
            return ValidationResult(
                successful = false,
                errorMessage = "El número de teléfono no puede exceder 15 dígitos"
            )
        }

        // Check for valid phone number pattern
        // This regex allows for international formats with +, spaces, dashes, and parentheses
        val phonePattern = Regex("^[+]?[0-9\\s\\-\\(\\)]{7,15}$")
        if (!phonePattern.matches(trimmedPhone)) {
            return ValidationResult(
                successful = false,
                errorMessage = "Por favor ingresa un número de teléfono válido"
            )
        }

        return ValidationResult(successful = true)
    }
} 