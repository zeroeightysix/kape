package me.zeroeightsix.kape.api

import me.zeroeightsix.kape.api.render.bind.BasicBindStack
import me.zeroeightsix.kape.api.render.bind.BindStack
import me.zeroeightsix.kape.api.render.renderer.LayerRenderer
import me.zeroeightsix.kape.api.state.UninitialisedWindowState
import me.zeroeightsix.kape.api.state.WindowState
import me.zeroeightsix.kape.api.state.layer.ForkOrderedLayer
import me.zeroeightsix.kape.api.util.Destroy

typealias ID = Any

@Suppress("MemberVisibilityCanBePrivate")
class Kape<P>(
    var windowState: WindowState = UninitialisedWindowState,
    private val renderer: LayerRenderer<P>,
    private val bindStack: BindStack = BasicBindStack(),
    private val contextSupplier: (Kape<P>) -> P?
) : ForkOrderedLayer<P>(), BindStack by bindStack, Destroy {

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

    fun frame(block: Kape<P>.() -> Unit) {
        if (!nextContext()) {
            error("Couldn't create new context")
        }
        block()
        renderAndRelease()
    }

    override fun destroy() {
        this.renderer.destroy()
    }

}
