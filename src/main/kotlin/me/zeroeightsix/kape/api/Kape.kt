package me.zeroeightsix.kape.api

import me.zeroeightsix.kape.api.context.Context
import me.zeroeightsix.kape.api.context.UninitialisedWindowState
import me.zeroeightsix.kape.api.context.WindowState
import me.zeroeightsix.kape.api.element.layer.ForkOrderedLayer
import me.zeroeightsix.kape.api.element.layer.LayerRenderer
import me.zeroeightsix.kape.impl.element.layer.GlLayerRenderer

typealias ID = Any

class Kape<P>(
    var windowState: WindowState = UninitialisedWindowState,
    private val renderer: LayerRenderer<P>,
    private val bindStack: BindStack = BasicBindStack(),
    private val contextSupplier: (Kape<P>) -> P?
) : ForkOrderedLayer<P>(), BindStack by bindStack {

    private var _context: P? = null

    override val context: P
        get() = this._context ?: error("Tried to access context before render initialisation")

    /**
     * Generates the next context. Returns `false` if this failed, and `true` if it succeeded.
     */
    fun nextContext(): Boolean {
        this._context = contextSupplier(this)
        return this._context != null
    }

    fun render() {
        renderer.render(this, bindStack = this)
    }

    fun renderAndRelease() {
        render()
        this.children.clear()
        this.windowState.clear()
    }

}

/**
 * An instance of [Kape] using the inbuilt implementations of context and renderer.
 *
 * Use this instance if you wish to co-operate with other projects that might be using Kape in the same environment,
 * unless the environment provides an instance of Kape.
 */
val kapeCommon = Kape(renderer = GlLayerRenderer()) { Context(it.windowState) }