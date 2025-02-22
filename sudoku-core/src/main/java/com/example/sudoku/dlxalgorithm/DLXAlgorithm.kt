package com.example.sudoku.dlxalgorithm

import com.example.sudoku.dlxalgorithm.model.DLXNode
import com.example.sudoku.dlxalgorithm.model.DataNode
import com.example.sudoku.dlxalgorithm.model.HeaderNode
import com.example.sudoku.dlxalgorithm.model.RootNode
import com.example.sudoku.dlxalgorithm.model.findBestColumn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive
import java.util.Stack

object DLXAlgorithm {
	fun RootNode.solve(
		shouldContinue: () -> Boolean = { true },
		collect: (ArrayList<Int>) -> Boolean,
	) {
		solveProblem(collect = collect, shouldContinue = shouldContinue)
	}

	fun RootNode.solveAll(): Collection<List<Int>> =
		ArrayList<List<Int>>().apply {
			solveProblem(
				collect = { add(it) },
				shouldContinue = { true },
			)
		}

	@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
	fun CoroutineScope.solveSuspend(
		rootNode: RootNode,
		limit: Int = 2,
	) = produce<List<Int>>(capacity = 2) {
		var count = 0
		rootNode.solveProblem(
			collect = { solution ->
				runCatching { channel.trySend(solution.toList()) }.isSuccess
				count++
				false
			},
			shouldContinue = { !isClosedForSend && isActive && count <= limit },
		)
	}

	private fun RootNode.solveProblem(
		solution: Stack<Int> = Stack<Int>(),
		collect: (ArrayList<Int>) -> Boolean,
		shouldContinue: () -> Boolean,
	) {
		if (!shouldContinue()) {
			return
		}

		val header: HeaderNode? = findBestColumn()
		when (header) {
			null -> {
				if (collect(ArrayList(solution))) return
				return
			}

			else -> {
				var node = header.down
				while (node != header as DLXNode && shouldContinue()) {
					solution.push((node as DataNode).rowId)
					var rightNode = node
					do {
						(rightNode as DataNode).header.cover()
						rightNode = rightNode.right
					} while (rightNode != node)

					solveProblem(solution, collect, shouldContinue)

					if (!shouldContinue()) {
						return
					}

					solution.pop()
					var startNode = node.left
					var leftNode = startNode
					do {
						(leftNode as DataNode).header.uncover()
						leftNode = leftNode.left
					} while (leftNode != startNode)

					node = node.down
				}
			}
		}
	}
}
