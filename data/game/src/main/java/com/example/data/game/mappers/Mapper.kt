package com.example.data.game.mappers

interface Mapper<I, O> {
	operator fun invoke(input: I): O
}
