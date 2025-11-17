package com.example.domain.creator

import com.example.sudokuslayer.domain.creator.ValidateSeedInputUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

class ValidateSeedInputUseCaseTest {
	private val validateSeedInputUseCase = ValidateSeedInputUseCase()

	@Test
	fun `validateSeedInputUseCase returns empty seedText and null parsedSeed for blank newText`() {
		val result = validateSeedInputUseCase.invoke("123", "")
		assertEquals("", result.seedText)
		assertNull(result.parsedSeed)
	}

	@Test
	fun `validateSeedInputUseCase returns newText as seedText and null parsedSeed for newText as dash`() {
		val result = validateSeedInputUseCase.invoke("123", "-")
		assertEquals("-", result.seedText)
		assertNull(result.parsedSeed)
	}

	@Test
	fun `validateSeedInputUseCase returns currentText and null parsedSeed for invalid number format`() {
		val result = validateSeedInputUseCase.invoke("123", "abc")
		assertEquals("123", result.seedText)
		assertNull(result.parsedSeed)
	}

	@Test
	fun `validateSeedInputUseCase returns currentText and null parsedSeed for number exceeding Long max value`() {
		val result = validateSeedInputUseCase.invoke("123", "${Long.MAX_VALUE}0")
		assertEquals("123", result.seedText)
		assertNull(result.parsedSeed)
	}

	@Test
	fun `validateSeedInputUseCase returns currentText and null parsedSeed for number below Long min value`() {
		val result = validateSeedInputUseCase.invoke("123", "${Long.MIN_VALUE}0")
		assertEquals("123", result.seedText)
		assertNull(result.parsedSeed)
	}

	@Test
	fun `validateSeedInputUseCase returns newText as seedText and parsedSeed for valid number within Long range`() {
		val result = validateSeedInputUseCase.invoke("123", "456")
		assertEquals("456", result.seedText)
		assertEquals(456L, result.parsedSeed)
	}
}
