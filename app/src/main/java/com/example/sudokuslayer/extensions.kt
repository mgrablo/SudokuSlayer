package com.example.sudokuslayer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.min
import kotlin.math.roundToInt

fun Color.lighten(factor: Float): Color {
	val argb = this.toArgb()
	val red = (argb.red * factor).roundToInt()
	val green = (argb.green * factor).roundToInt()
	val blue = (argb.blue * factor).roundToInt()

	return Color(
		android.graphics.Color.argb(
			argb.alpha,
			min(red, 255),
			min(green, 255),
			min(blue, 255),
		),
	)
}

fun createAnnotatedString(
	input: String,
	singleQuoteStyle: SpanStyle = SpanStyle(color = Color.Red),
	asteriskStyle: SpanStyle = SpanStyle(color = Color.DarkGray),
	angleBracketStyle: SpanStyle = SpanStyle(color = Color.Yellow),
	squareBracketStyle: SpanStyle = SpanStyle(color = Color.LightGray),
	curlyBracketStyle: SpanStyle = SpanStyle(color = Color.Magenta),
): AnnotatedString {
	val singleQuotePattern = "'(.*?)'".toRegex() // 'column 4'
	val asteriskPattern = "\\*(.*?)\\*".toRegex() // *HintType*
	val angleBracketPattern = "<(.*?)>".toRegex() // number <4> in cell [2, 1]
	val squareBracketPattern = "\\[(.*?)]".toRegex() // [row, col] - [3, 4]
	val curlyBracketPattern = "\\{(.*?)\\}".toRegex() // columns {1, 2, 3}

	val patterns =
		listOf(
			Triple(singleQuotePattern, singleQuoteStyle, true),
			Triple(asteriskPattern, asteriskStyle, true),
			Triple(angleBracketPattern, angleBracketStyle, true),
			Triple(squareBracketPattern, squareBracketStyle, false),
			Triple(curlyBracketPattern, curlyBracketStyle, false),
		)

	val matches =
		patterns.flatMap { (pattern, style, removeMarks) ->
			pattern.findAll(input).map { matchResult ->
				val start = matchResult.range.first
				val end = matchResult.range.last + 1
				val content = if (removeMarks) matchResult.groupValues[1] else matchResult.value
				Triple(start, end, content to style)
			}
		}.sortedBy { it.first }

	return buildAnnotatedString {
		var lastIndex = 0

		matches.forEach { (start, end, contentAndStyle) ->
			val (content, style) = contentAndStyle

			if (start > lastIndex) {
				append(input.substring(lastIndex, start))
			}

			withStyle(style) {
				append(content)
			}

			lastIndex = end
		}

		if (lastIndex < input.length) {
			append(input.substring(lastIndex))
		}
	}
}
