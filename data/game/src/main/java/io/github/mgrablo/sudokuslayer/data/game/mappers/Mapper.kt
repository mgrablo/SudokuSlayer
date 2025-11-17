package io.github.mgrablo.sudokuslayer.data.game.mappers

interface Mapper<I, O> {
	operator fun invoke(input: I): O
}
