package me.zeroeightsix.kape.impl

import me.zeroeightsix.kape.api.Kape
import me.zeroeightsix.kape.api.state.Context
import me.zeroeightsix.kape.impl.render.renderer.GlLayerRenderer

/**
 * An instance of [Kape] using the inbuilt implementations of context and renderer.
 *
 * Use this instance if you wish to co-operate with other projects that might be using Kape in the same environment,
 * unless the environment provides an instance of Kape.
 */
val kapeCommon = Kape(renderer = GlLayerRenderer()) { Context(it.windowState) }