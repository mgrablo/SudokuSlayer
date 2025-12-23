package io.github.mgrablo.sudokucore.hints

object HintMessageFormatter {
	/**
	 * Formats a pattern string with placeholders like {0}, {1} into a list of HintExplanationPart.
	 *
	 * @param pattern The string pattern containing placeholders (e.g., "Focus on the cell {0}!")
	 * @param args The HintExplanationPart arguments to replace the placeholders with.
	 * @return A list of HintExplanationPart with text and arguments interleaved.
	 */
	fun format(pattern: String, vararg args: HintExplanationPart): List<HintExplanationPart> {
		val parts = mutableListOf<HintExplanationPart>()
		val regex = "\\{(\\d+)\\}".toRegex()
		var lastIndex = 0

		regex.findAll(pattern).forEach { matchResult ->
			// Add text before the match
			if (matchResult.range.first > lastIndex) {
				parts.add(
					HintExplanationPart.Text(pattern.substring(lastIndex, matchResult.range.first)),
				)
			}

			// Add the argument
			val argIndex = matchResult.groupValues[1].toInt()
			if (argIndex in args.indices) {
				parts.add(args[argIndex])
			} else {
				// Keep the placeholder if argument is missing
				parts.add(HintExplanationPart.Text(matchResult.value))
			}

			lastIndex = matchResult.range.last + 1
		}

		// Add remaining text
		if (lastIndex < pattern.length) {
			parts.add(HintExplanationPart.Text(pattern.substring(lastIndex)))
		}

		return parts
	}
}
