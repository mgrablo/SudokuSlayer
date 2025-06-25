package com.example.sudoku

fun symmetricDifference(lists: List<Set<Int>>): Set<Int> = lists
	.flatMap { it.toSet() }
	.groupingBy { it }
	.eachCount()
	.filter { it.value == 1 }
	.keys
