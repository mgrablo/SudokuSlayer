package com.example.data.game.mappers

import com.example.domain.game.repositories.Operation
import data.game.ProtoOperation

fun Operation.toProtoOperation(): ProtoOperation =
	ProtoOperation
		.newBuilder()
		.setId(id)
		.setRow(cellRow)
		.setColumn(cellColumn)
		.setValue(value)
		.build()

fun ProtoOperation.toOperation(): Operation =
	Operation(
		id = id,
		cellRow = row,
		cellColumn = column,
		value = value,
	)
