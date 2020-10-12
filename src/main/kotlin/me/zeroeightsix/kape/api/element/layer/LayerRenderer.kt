package me.zeroeightsix.kape.api.element.layer

import me.zeroeightsix.kape.api.BindStack
import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.NoBindStack
import me.zeroeightsix.kape.impl.gl.VAO
import java.util.*

interface LayerRenderer<P> {

    fun render(layer: Layer<P>, id: ID = layer, bindStack: BindStack = NoBindStack)

}