package io.github.mgrablo.sudokucore.hints

/**
 * Interface for providing localized strings to the hint explanation strategies.
 * This allows the core module to generate explanations with localizable text.
 */
interface HintStringProvider {
	fun getString(key: HintStringKey, vararg formatArgs: Any): String

	// Default implementation that just returns the key name with format args
	// This is used when a real implementation is not provided
	companion object {
		val DEFAULT = object : HintStringProvider {
			override fun getString(key: HintStringKey, vararg formatArgs: Any): String =
				if (formatArgs.isEmpty()) {
					key.name
				} else {
					String.format(key.name, *formatArgs)
				}
		}
	}
}

/**
 * Enum of string keys used in hint explanations.
 * These correspond to string resource IDs in the Android resources.
 */
enum class HintStringKey {

	// Naked Single
	NAKED_SINGLE_STEP_1,
	NAKED_SINGLE_STEP_2,
	NAKED_SINGLE_STEP_3,

	// Hidden Single
	HIDDEN_SINGLE_STEP_1,
	HIDDEN_SINGLE_STEP_2,
	HIDDEN_SINGLE_STEP_3,

	// Claiming Candidate
	CLAIMING_CANDIDATE_STEP_1,
	CLAIMING_CANDIDATE_STEP_2,
	CLAIMING_CANDIDATE_STEP_3,
	CLAIMING_CANDIDATE_STEP_4,

	// Pointing Candidate
	POINTING_CANDIDATE_STEP_1,
	POINTING_CANDIDATE_STEP_2,
	POINTING_CANDIDATE_STEP_3,
	POINTING_CANDIDATE_STEP_4,

	// Scope parts
	ROW,
	COLUMN,

	// Technique names
	TECHNIQUE_NAKED_SINGLE,
	TECHNIQUE_HIDDEN_SINGLE,
	TECHNIQUE_POINTING_CANDIDATE,
	TECHNIQUE_CLAIMING_CANDIDATE,
}
