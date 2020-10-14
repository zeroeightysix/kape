package me.zeroeightsix.kape.impl

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.render.`object`.PrimitiveType.LINES
import me.zeroeightsix.kape.api.render.`object`.VertexColour
import me.zeroeightsix.kape.api.render.`object`.invoke
import me.zeroeightsix.kape.api.state.Context
import me.zeroeightsix.kape.api.state.layer.Layer
import me.zeroeightsix.kape.api.util.black
import me.zeroeightsix.kape.api.util.blue
import me.zeroeightsix.kape.api.util.green
import me.zeroeightsix.kape.api.util.math.*
import me.zeroeightsix.kape.api.util.red

fun Layer<Context>.window(title: String = "Kape window", id: ID = title) {
    val ctx = this.context

    if (ctx.windowState.mouseDelta != Vec2d(0.0))
        ctx.dirty()

    val cursor = ctx.windowState.mouse.toVec2f() + Vec2f(1f)

    ctx {
        VertexColour {
            push(
                LINES,
                arrayOf(
                    (cursor - Vec2f(10f)) % red, // 0
                    (cursor + Vec2f(10f, -10f)) % blue, // 1
                    (cursor + Vec2f(-10f, 10f)) % black, // 2
                    (cursor + Vec2f(10f)) % green // 3
                ),
                intArrayOf(0, 1, 0, 2, 0, 3, 1, 3, 2, 3)
            )
        }
    }
}