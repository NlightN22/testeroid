package space.active.testeroid.screens.edittest

import space.active.testeroid.R

class ValidateSelected {
    fun valid(listSelected: List<Boolean>): ValidationResult{
        if (listSelected.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = R.string.edit_validation_empty
            )
        }

        if (listSelected.filter { it }.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = R.string.edit_validation_less_one
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}

data class ValidationResult(
    val successful: Boolean,
    val errorMessage: Int? = null
)