package me.zeroeightsix.kape.api.element

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.context.Context
import me.zeroeightsix.kape.api.element.layer.Layer
import me.zeroeightsix.kape.api.gl.GlPrimitive
import me.zeroeightsix.kape.api.gl.PrimitiveType
import me.zeroeightsix.kape.api.gl.Vertex
import me.zeroeightsix.kape.api.math.times
import me.zeroeightsix.kape.api.math.unaryMinus
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

const val quartCircle = PI / 2

fun Layer<Context>.window(title: String = "Kape window", id: ID = title) {
    val ctx = this.context
    ctx.dirty()

    val time = System.currentTimeMillis() / 1000.0
    val pos1 = Vertex(cos(time).toFloat(), sin(time).toFloat())
    val pos2 = Vertex(cos(time + quartCircle).toFloat(), sin(time + quartCircle).toFloat())
    val pos3 = -pos1
    val pos4 = -pos2

    ctx draw {
        GlPrimitive(
            PrimitiveType.LINE_LOOP, arrayOf(
            pos1,
            pos2,
            pos3,
            pos4
        ))
    }

    ctx draw {
        GlPrimitive(
            PrimitiveType.LINE_LOOP, arrayOf(
            pos1 * 0.7f,
            pos2 * 0.5f,
            pos3 * 0.3f
        ))
    }
}