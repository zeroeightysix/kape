package me.zeroeightsix.kape.api

import me.zeroeightsix.kape.api.context.Context
import me.zeroeightsix.kape.api.element.layer.ForkOrderedLayer
import me.zeroeightsix.kape.api.element.layer.LayerRenderer
import me.zeroeightsix.kape.impl.element.layer.GlLayerRenderer

typealias ID = Any

class Kape<P>(private val renderer: LayerRenderer<P>, override val context: P) : ForkOrderedLayer<P>(context = context) {

    private val bindStackMap = mutableMapOf<ID, ArrayDeque<Bind>>()

    private fun getBindStack(bind: Bind) = bindStackMap.getOrPut(bind.bindTypeId) { ArrayDeque() }

    fun Bind.bindScoped(block: () -> Unit) {
        val stack = getBindStack(this)

        stack.add(this)
        this.bind()

        block()

        stack.removeLast()
        val last = stack.lastOrNull()
        if (last != null)
            last.bind()
        else
            this.resetBind()
    }

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
val kapeCommon = Kape(GlLayerRenderer(), Context())