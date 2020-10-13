package me.zeroeightsix.kape.api.element

import me.zeroeightsix.kape.api.*
import me.zeroeightsix.kape.api.context.Context
import me.zeroeightsix.kape.api.element.layer.Layer
import me.zeroeightsix.kape.api.gl.PrimitiveType.LINES
import me.zeroeightsix.kape.api.gl.PrimitiveType.LINE_LOOP
import me.zeroeightsix.kape.api.gl.Vertex
import me.zeroeightsix.kape.api.gl.VertexColour
import me.zeroeightsix.kape.api.gl.invoke
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

    ctx {
        VertexColour {
            push(
                LINE_LOOP,
                pos1 % white,
                pos2 % red,
                pos3 % green,
                pos4 % blue
            )
        }
    }
}