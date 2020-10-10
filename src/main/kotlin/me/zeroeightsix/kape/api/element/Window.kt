package me.zeroeightsix.kape.api.element

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.context.Context
import me.zeroeightsix.kape.api.element.layer.Layer

fun Layer<Context>.window(title: String = "Kape window", id: ID = title) {
    val ctx = this.context

    ctx draw {
        Vertex(0f, 0f) lineTo Vertex(1f, 1f)
    }
}