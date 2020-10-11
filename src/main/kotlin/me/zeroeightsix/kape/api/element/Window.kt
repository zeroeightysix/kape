package me.zeroeightsix.kape.api.element

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.context.Context
import me.zeroeightsix.kape.api.element.layer.Layer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

const val halfCircle = PI

fun Layer<Context>.window(title: String = "Kape window", id: ID = title) {
    val ctx = this.context
    ctx.dirty()

    val time = System.currentTimeMillis() / 1000.0
    val x1 = cos(time).toFloat()
    val y1 = sin(time).toFloat()
    val x2 = cos(time + halfCircle).toFloat()
    val y2 = sin(time + halfCircle).toFloat()

    ctx draw {
        Vertex(x1, y1) lineTo Vertex(x2, y2)
    }
}