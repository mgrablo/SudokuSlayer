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
	// General hint parts
	FOCUS_ON_CELL,
	FOCUS_ON_BLOCK,
	THEREFORE,
	MUST_CONTAIN,
	THE_CELL_AT,
	IN,
	AFFECTED_CELLS_ARE,
	NO_OTHER_EMPTY_CELLS,
	CAN_ONLY_BE_PLACED_IN,
	CANNOT_BE_PLACED_IN,
	ONLY_POSSIBLE_CANDIDATE,
	FOLLOWING_CELLS,
	SINCE,
	THUS_REMOVE_FROM,
	THEY_ARE_BLOCKED_BY,
	CAN_ONLY_APPEAR_IN_THE,
	CANNOT_APPEAR_IN_THE,
	SINCE_CONFINED_TO,
	ONE_POSSIBLE_CANDIDATE,

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

	// Scope parts
	ROW,
	COLUMN,
	BLOCK,
	BLOCK_PART,

	// Position descriptions
	TOP,
	MIDDLE,
	BOTTOM,
	LEFT,
	CENTER,
	RIGHT,

	// Other hints context
	SAME_ROW,
	SAME_COLUMN,
	SAME_BLOCK,

	// Technique names
	TECHNIQUE_NAKED_SINGLE,
	TECHNIQUE_HIDDEN_SINGLE,
	TECHNIQUE_POINTING_CANDIDATE,
	TECHNIQUE_CLAIMING_CANDIDATE,
}
