package com.example.data.game.mappers

import com.example.domain.core.HintLog
import com.example.sudoku.solver.HintExplanationPart
import com.example.sudoku.solver.HintExplanationStep
import com.example.sudoku.solver.ScopeType
import data.game.ProtoHintExplanationPart
import data.game.ProtoHintExplanationStep
import data.game.ProtoHintLog
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

fun HintLog.toProtoHintLog(): ProtoHintLog {
	val builder = ProtoHintLog
		.newBuilder()
		.setId(id)
		.setHint(hint.toProtoHint())
		.setIsUserGuessed(isUserGuessed)
		.setIsRevealed(isRevealed)

	// Convert structured explanation if available
	if (structuredExplanation.isNotEmpty()) {
		builder.addAllStructuredExplanation(
			structuredExplanation.map { step -> step.toProtoHintExplanationStep() },
		)
	}

	return builder.build()
}

fun ProtoHintLog.toHintLog(): HintLog = HintLog(
	id = id,
	hint = hint.toHint(),
	isUserGuessed = isUserGuessed,
	isRevealed = isRevealed,
	structuredExplanation = if (structuredExplanationCount > 0) {
		structuredExplanationList.map { it.toHintExplanationStep() }.toPersistentList()
	} else {
		persistentListOf()
	},
)

// Convert domain HintExplanationStep to Proto HintExplanationStep
fun HintExplanationStep.toProtoHintExplanationStep(): ProtoHintExplanationStep {
	val builder = ProtoHintExplanationStep.newBuilder()

	parts.forEach { part ->
		builder.addParts(part.toProtoHintExplanationPart())
	}

	return builder.build()
}

// Convert Proto HintExplanationStep to domain HintExplanationStep
fun ProtoHintExplanationStep.toHintExplanationStep(): HintExplanationStep = HintExplanationStep(
	parts = partsList.map { it.toHintExplanationPart() }.toPersistentList(),
)

// Convert domain HintExplanationPart to Proto HintExplanationPart
fun HintExplanationPart.toProtoHintExplanationPart(): ProtoHintExplanationPart {
	val builder = ProtoHintExplanationPart.newBuilder()

	when (this) {
		is HintExplanationPart.Text -> {
			builder.text = ProtoHintExplanationPart.TextPart.newBuilder()
				.setContent(content)
				.build()
		}
		is HintExplanationPart.CellCoordinate -> {
			builder.cellCoordinate = ProtoHintExplanationPart.CellCoordinatePart.newBuilder()
				.setRow(row)
				.setCol(col)
				.build()
		}
		is HintExplanationPart.CellCoordinatesGroup -> {
			val cellsBuilder = ProtoHintExplanationPart.CellCoordinatesGroupPart.newBuilder()
			cells.forEach { (row, col) ->
				cellsBuilder.addCells(
					ProtoHintExplanationPart.CellCoordinatePart.newBuilder()
						.setRow(row)
						.setCol(col)
						.build(),
				)
			}
			builder.cellCoordinatesGroup = cellsBuilder.build()
		}
		is HintExplanationPart.Value -> {
			builder.value = ProtoHintExplanationPart.ValuePart.newBuilder()
				.setValue(value)
				.build()
		}
		is HintExplanationPart.TechniqueName -> {
			builder.techniqueName = ProtoHintExplanationPart.TechniqueNamePart.newBuilder()
				.setName(name)
				.build()
		}
		is HintExplanationPart.ScopeReference -> {
			val scopeBuilder = ProtoHintExplanationPart.ScopeReferencePart.newBuilder()
				.setType(
					when (type) {
						ScopeType.ROW -> ProtoHintExplanationPart.ScopeReferencePart.ScopeType.ROW
						ScopeType.COLUMN -> ProtoHintExplanationPart.ScopeReferencePart.ScopeType.COLUMN
						ScopeType.BLOCK -> ProtoHintExplanationPart.ScopeReferencePart.ScopeType.BLOCK
						ScopeType.BLOCK_PART -> ProtoHintExplanationPart.ScopeReferencePart.ScopeType.BLOCK_PART
					},
				)

			// Only set index if it's not null
			index?.let { scopeBuilder.setIndex(it) }

			builder.scopeReference = scopeBuilder.build()
		}
		is HintExplanationPart.ValueGroup -> {
			builder.valueGroup = ProtoHintExplanationPart.ValueGroupPart.newBuilder()
				.addAllValues(values)
				.build()
		}
	}

	return builder.build()
}

// Convert Proto HintExplanationPart to domain HintExplanationPart
fun ProtoHintExplanationPart.toHintExplanationPart(): HintExplanationPart = when (partCase) {
	ProtoHintExplanationPart.PartCase.TEXT ->
		HintExplanationPart.Text(text.content)

	ProtoHintExplanationPart.PartCase.CELL_COORDINATE ->
		HintExplanationPart.CellCoordinate(cellCoordinate.row, cellCoordinate.col)

	ProtoHintExplanationPart.PartCase.CELL_COORDINATES_GROUP -> {
		val cells = cellCoordinatesGroup.cellsList.map { Pair(it.row, it.col) }
		HintExplanationPart.CellCoordinatesGroup(cells)
	}

	ProtoHintExplanationPart.PartCase.VALUE ->
		HintExplanationPart.Value(value.value)

	ProtoHintExplanationPart.PartCase.TECHNIQUE_NAME ->
		HintExplanationPart.TechniqueName(techniqueName.name)

	ProtoHintExplanationPart.PartCase.SCOPE_REFERENCE -> {
		val type = when (scopeReference.type) {
			ProtoHintExplanationPart.ScopeReferencePart.ScopeType.ROW -> ScopeType.ROW
			ProtoHintExplanationPart.ScopeReferencePart.ScopeType.COLUMN -> ScopeType.COLUMN
			ProtoHintExplanationPart.ScopeReferencePart.ScopeType.BLOCK -> ScopeType.BLOCK
			ProtoHintExplanationPart.ScopeReferencePart.ScopeType.BLOCK_PART -> ScopeType.BLOCK_PART
			else -> ScopeType.ROW // Default to ROW for unspecified
		}

		// Handle optional index
		val index = if (scopeReference.hasIndex()) scopeReference.index else null

		HintExplanationPart.ScopeReference(type, index)
	}

	ProtoHintExplanationPart.PartCase.VALUE_GROUP ->
		HintExplanationPart.ValueGroup(valueGroup.valuesList)

	else -> {
		System.err.println("Unknown part case: $partCase")
		throw IllegalArgumentException("Unknown part case: $partCase")
	}
}
