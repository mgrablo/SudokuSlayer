package io.github.mgrablo.sudokuslayer.domain.game.usecases.hint

import io.github.mgrablo.sudokuslayer.domain.core.HintLog
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

class RevealLastHintLogUseCase {
	operator fun invoke(hintLogs: List<HintLog>): PersistentList<HintLog> {
		val mutableLogs = hintLogs.toMutableList()
		val lastHintId = hintLogs.lastIndex
		mutableLogs[lastHintId] =
			mutableLogs[lastHintId].copy(
				isRevealed = true,
			)
		return mutableLogs.toPersistentList()
	}
}
