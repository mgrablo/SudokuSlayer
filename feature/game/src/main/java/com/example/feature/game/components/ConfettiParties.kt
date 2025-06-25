package com.example.feature.game.components

import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Rotation
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlin.time.Duration.Companion.milliseconds

fun explode(): List<Party> = listOf(
	Party(
		speed = 0f,
		maxSpeed = 30f,
		damping = 0.9f,
		spread = 360,
		colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
		emitter = Emitter(duration = 100.milliseconds).max(100),
		position = Position.Relative(0.5, 0.35),
		rotation = Rotation.enabled()
	),
)
