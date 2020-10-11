package me.zeroeightsix.kape.api

import me.zeroeightsix.kape.api.context.Context
import me.zeroeightsix.kape.api.element.layer.ForkOrderedLayer
import me.zeroeightsix.kape.api.element.layer.LayerRenderer
import me.zeroeightsix.kape.impl.element.layer.GlLayerRenderer

typealias ID = Any

class Kape<P>(
    private val rendererSupplier: (Kape<P>) -> LayerRenderer<P>,
    val bindStack: BindStack = BasicBindStack(),
    val contextSupplier: () -> P
) : ForkOrderedLayer<P>(context = contextSupplier()), BindStack by bindStack {

    private var _context: P = super.context
    private val renderer = rendererSupplier(this)

    override val context: P
        get() = this._context

    fun render() {
        renderer.render(this)
    }

    fun renderAndRelease() {
        render()
        this.children.clear()
        this._context = contextSupplier()
    }

}

/**
 * An instance of [Kape] using the inbuilt implementations of context and renderer.
 *
 * Use this instance if you wish to co-operate with other projects that might be using Kape in the same environment,
 * unless the environment provides an instance of Kape.
 */
val kapeCommon = Kape({ GlLayerRenderer(bindStack = it) }) { Context() }