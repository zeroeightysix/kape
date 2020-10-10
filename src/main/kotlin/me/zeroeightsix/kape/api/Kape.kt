package me.zeroeightsix.kape.api

import me.zeroeightsix.kape.api.element.layer.ForkOrderedLayer
import me.zeroeightsix.kape.api.element.layer.LayerRenderer
import me.zeroeightsix.kape.impl.element.layer.GlLayerRenderer

typealias ID = Any

class Kape(val renderer: LayerRenderer) : ForkOrderedLayer() {

    fun render() {
        renderer.render(this)
    }

}

/**
 * An instance of [Kape] where the default constructor parameters was used.
 *
 * Use this instance if you wish to co-operate with other projects that might be using Kape in the same environment,
 * unless the environment provides an instance of Kape.
 */
val kapeCommon = Kape(GlLayerRenderer())