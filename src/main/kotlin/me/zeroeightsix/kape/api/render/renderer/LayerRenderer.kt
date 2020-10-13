package me.zeroeightsix.kape.api.render.renderer

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.render.bind.BindStack
import me.zeroeightsix.kape.api.render.bind.NoBindStack
import me.zeroeightsix.kape.api.state.layer.Layer
import me.zeroeightsix.kape.api.util.Destroy

interface LayerRenderer<P> : Destroy {

    fun render(layer: Layer<P>, id: ID = layer, bindStack: BindStack = NoBindStack)

}