package me.zeroeightsix.kape.impl

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.render.`object`.PrimitiveType.LINE_LOOP
import me.zeroeightsix.kape.api.render.`object`.Vertex
import me.zeroeightsix.kape.api.render.`object`.VertexColour
import me.zeroeightsix.kape.api.render.`object`.invoke
import me.zeroeightsix.kape.api.state.Context
import me.zeroeightsix.kape.api.state.layer.Layer
import me.zeroeightsix.kape.api.util.blue
import me.zeroeightsix.kape.api.util.green
import me.zeroeightsix.kape.api.util.math.unaryMinus
import me.zeroeightsix.kape.api.util.red
import me.zeroeightsix.kape.api.util.white
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