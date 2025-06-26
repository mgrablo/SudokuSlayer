package com.example.feature.game.components

import io.github.vinceglb.confettikit.core.Angle
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.Position
import io.github.vinceglb.confettikit.core.Rotation
import io.github.vinceglb.confettikit.core.Spread
import io.github.vinceglb.confettikit.core.emitter.Emitter
import io.github.vinceglb.confettikit.core.models.Shape
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal object ConfettiParties {
	fun explode(): List<Party> = listOf(
		Party(
			speed = 0f,
			maxSpeed = 30f,
			damping = 0.9f,
			spread = 360,
			colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
			emitter = Emitter(duration = 100.milliseconds).max(100),
			position = Position.Relative(0.5, 0.35),
			rotation = Rotation.enabled(),
			fadeOutEnabled = false,
			shapes = listOf(Shape.Square, Shape.Circle),
			timeToLive = 7000L,
		),
	)

	fun parade(): List<Party> {
		val party = Party(
			speed = 10f,
			maxSpeed = 30f,
			damping = 0.9f,
			angle = Angle.RIGHT - 45,
			spread = Spread.SMALL,
			colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
			emitter = Emitter(duration = 5.seconds).perSecond(30),
			position = Position.Relative(0.0, 0.5),
		)

		return listOf(
			party,
			party.copy(
				angle = party.angle - 90, // flip angle from right to left
				position = Position.Relative(1.0, 0.5),
			),
		)
	}
}
