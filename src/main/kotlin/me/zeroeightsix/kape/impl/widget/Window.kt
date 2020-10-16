@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")

package me.zeroeightsix.kape.impl.widget

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.render.`object`.PrimitiveType.TRIANGLES
import me.zeroeightsix.kape.api.render.`object`.VertexColour
import me.zeroeightsix.kape.api.render.`object`.invoke
import me.zeroeightsix.kape.api.state.Context
import me.zeroeightsix.kape.api.state.layer.Layer
import me.zeroeightsix.kape.api.util.Colour
import me.zeroeightsix.kape.api.util.math.Vec2f
import me.zeroeightsix.kape.api.util.math.justX
import me.zeroeightsix.kape.api.util.math.justY
import me.zeroeightsix.kape.api.util.math.offsets

private const val transparentGrey: Colour = 0x222222AAu

@OptIn(ExperimentalUnsignedTypes::class)
fun Layer<Context>.window(title: String = "Kape window", id: ID = title) {
    val ctx = this.context

    val windowSize = ctx.getState<Vec2f>(id, "size") ?: Vec2f(100f).also {
        // Immediately set it if it was not present
        ctx.setState(id, "size", it)
    }

    val windowPosition = ctx.getState<Vec2f>(id, "position") ?: Vec2f(10f).also {
        ctx.setState(id, "position", it)
    }

    ctx {
        VertexColour {
            push(
                TRIANGLES,
                windowPosition.offsets(
                    inclusive = true,   // 0
                    windowSize,         // 1
                    windowSize.justX,   // 2
                    windowSize.justY    // 3
                ),
                intArrayOf(0, 1, 2, 0, 3, 1),
                colour = transparentGrey
            )
        }
    }
}