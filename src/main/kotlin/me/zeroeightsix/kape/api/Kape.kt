package me.zeroeightsix.kape.api

import me.zeroeightsix.kape.api.render.bind.BasicBindStack
import me.zeroeightsix.kape.api.render.bind.BindStack
import me.zeroeightsix.kape.api.render.renderer.LayerRenderer
import me.zeroeightsix.kape.api.state.Clone
import me.zeroeightsix.kape.api.state.UninitialisedWindowState
import me.zeroeightsix.kape.api.state.WindowState
import me.zeroeightsix.kape.api.state.layer.ForkOrderedLayer
import me.zeroeightsix.kape.api.util.Destroy

typealias ID = Any

@Suppress("MemberVisibilityCanBePrivate")
class Kape<P : Clone<P>>(
    var windowState: WindowState = UninitialisedWindowState,
    private val renderer: LayerRenderer<P>,
    private var _context: P,
    private val bindStack: BindStack = BasicBindStack()
) : ForkOrderedLayer<P>(), BindStack by bindStack, Destroy {

    override val context: P
        get() = _context

    fun nextContext() {
        this._context = this.context.clone()
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
        nextContext()
        block()
        renderAndRelease()
    }

    override fun destroy() {
        this.renderer.destroy()
    }

}
