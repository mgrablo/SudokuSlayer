package com.example.sudokuslayer.domain.creator

import java.math.BigInteger

class ValidateSeedInputUseCase {
	companion object {
		private val MAX_LONG_AS_BIG_INT = BigInteger.valueOf(Long.MAX_VALUE)
		private val MIN_LONG_AS_BIG_INT = BigInteger.valueOf(Long.MIN_VALUE)
	}

	data class ValidationResult(val seedText: String, val parsedSeed: Long?)

	operator fun invoke(currentText: String, newText: String): ValidationResult {
		if (newText.isBlank()) {
			return ValidationResult(seedText = "", parsedSeed = null)
		}
		if (newText == "-") {
			return ValidationResult(
				seedText = newText,
				parsedSeed = null,
			)
		}
		val prospectiveNumber = try {
			BigInteger(newText)
		} catch (e: NumberFormatException) {
			return ValidationResult(
				seedText = currentText,
				parsedSeed = null,
			)
		}
		if (prospectiveNumber > MAX_LONG_AS_BIG_INT || prospectiveNumber < MIN_LONG_AS_BIG_INT) {
			return ValidationResult(
				seedText = currentText,
				parsedSeed = null,
			)
		}

		return ValidationResult(
			seedText = newText,
			parsedSeed = prospectiveNumber.toLong(),
		)
	}
}
