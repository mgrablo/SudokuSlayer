package io.github.mgrablo.sudokuslayer.data.game.mappers

import android.util.Log
import io.github.mgrablo.sudokuslayer.data.game.ProtoHintLog
import io.github.mgrablo.sudokuslayer.domain.core.HintLog

fun HintLog.toProtoHintLog(): ProtoHintLog {
	val builder = ProtoHintLog
		.newBuilder()
		.setId(id)
		.setHint(hint.toProtoHint())
		.setIsUserGuessed(isUserGuessed)
		.setIsRevealed(isRevealed)

	return builder.build()
}

fun ProtoHintLog.toHintLog(): Result<HintLog> = hint.toHint().onFailure {
	Log.e("ProtoHintLogMapper", "Failed to convert ProtoHint to Hint for HintLog with id $id", it)
}.map { h ->
	HintLog(
		id = id,
		hint = h,
		isUserGuessed = isUserGuessed,
		isRevealed = isRevealed,
	)
}
