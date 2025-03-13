package com.example.data.game.mappers

import com.example.data.game.models.HintLog
import data.game.ProtoHintLog
import kotlinx.collections.immutable.toPersistentList

fun HintLog.toProtoHintLog(): ProtoHintLog =
	ProtoHintLog
		.newBuilder()
		.setHint(hint.toProtoHint())
		.setIsUserGuessed(isUserGuessed)
		.setIsRevealed(isRevealed)
		.addAllExplanation(
			explanation,
		).build()

fun ProtoHintLog.toHintLog(): HintLog =
	HintLog(
		hint = hint.toHint(),
		isUserGuessed = isUserGuessed,
		isRevealed = isRevealed,
		explanation = explanationList.toPersistentList(),
	)
