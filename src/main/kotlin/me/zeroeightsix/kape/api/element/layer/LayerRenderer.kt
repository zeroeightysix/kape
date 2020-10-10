package me.zeroeightsix.kape.api.element.layer

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.impl.gl.VAO
import java.util.*

interface LayerRenderer {

    fun render(layer: Layer, id: ID = layer)

}