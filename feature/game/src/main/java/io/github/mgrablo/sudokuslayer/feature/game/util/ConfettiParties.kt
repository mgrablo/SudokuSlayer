package io.github.mgrablo.sudokuslayer.feature.game.util

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
	fun explode(position: Position = Position.Relative(0.5, 0.5)): List<Party> = listOf(
		Party(
			speed = 0f,
			maxSpeed = 30f,
			damping = 0.9f,
			spread = 360,
			colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
			emitter = Emitter(duration = 100.milliseconds).max(100),
			position = position,
			rotation = Rotation.Companion.enabled(),
			fadeOutEnabled = false,
			shapes = listOf(Shape.Square, Shape.Circle),
			timeToLive = 7000L,
		),
	)

	fun parade(
		position1: Position = Position.Relative(0.0, 0.3),
		position2: Position = Position.Relative(1.0, 0.3),
	): List<Party> {
		val party = Party(
			speed = 10f,
			maxSpeed = 30f,
			damping = 0.9f,
			angle = Angle.Companion.RIGHT - 45,
			spread = Spread.Companion.SMALL,
			colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
			emitter = Emitter(duration = 1.seconds).perSecond(30),
			position = position1,
		)

		return listOf(
			party,
			party.copy(
				angle = party.angle - 90, // flip angle from right to left
				position = position2,
			),
		)
	}

	fun fountain(position: Position = Position.Relative(0.5, 0.3)): List<Party> {
		val party = Party(
			speed = 10f,
			maxSpeed = 30f,
			damping = 0.9f,
			angle = Angle.Companion.TOP,
			spread = Spread.Companion.SMALL,
			colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
			emitter = Emitter(duration = 2.seconds).perSecond(30),
			position = position,
		)
		return listOf(party)
	}

	fun rain(): List<Party> = listOf(
		Party(
			speed = 0f,
			maxSpeed = 15f,
			damping = 0.9f,
			angle = Angle.Companion.BOTTOM,
			spread = Spread.Companion.ROUND,
			colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
			emitter = Emitter(duration = 2.5.seconds).perSecond(100),
			position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
		),
	)
}
