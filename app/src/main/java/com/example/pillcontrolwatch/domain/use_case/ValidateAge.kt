package com.example.pillcontrolwatch.domain.use_case

class ValidateAge {
    fun execute(age: String): ValidationResult {
        // Trim the age to handle any leading or trailing spaces
        val trimmedAge = age.trim()

        // Check if age is blank (optional field)
        if (trimmedAge.isBlank()) {
            return ValidationResult(successful = true) // Age is optional
        }

        // Check if age contains only digits
        if (!trimmedAge.matches(Regex("^[0-9]+$"))) {
            return ValidationResult(
                successful = false,
                errorMessage = "La edad debe ser un número válido"
            )
        }

        // Convert to integer for range validation
        val ageNumber = trimmedAge.toIntOrNull()
        if (ageNumber == null) {
            return ValidationResult(
                successful = false,
                errorMessage = "La edad debe ser un número válido"
            )
        }

        // Check for reasonable age range (1-120 years)
        if (ageNumber < 1) {
            return ValidationResult(
                successful = false,
                errorMessage = "La edad debe ser mayor a 0"
            )
        }

        if (ageNumber > 120) {
            return ValidationResult(
                successful = false,
                errorMessage = "La edad no puede ser mayor a 120 años"
            )
        }

        return ValidationResult(successful = true)
    }
} 