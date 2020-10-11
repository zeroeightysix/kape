package me.zeroeightsix.kape.api.context

import me.zeroeightsix.kape.api.element.GlPrimitive

class Context : Reproducible<Context> {
    /**
     * Whether or not the context was modified compared to the previous iteration of contexts
     */
    var dirty = false

    fun dirty() {
        dirty = true
    }

    private val queue = ArrayDeque<() -> GlPrimitive>()

    fun drawAll(): List<GlPrimitive> = queue.map { it() }

    infix fun draw(supplier: () -> GlPrimitive) = queue.add(supplier).let { Unit }

    operator fun plusAssign(supplier: () -> GlPrimitive) = this.draw(supplier)

    override fun createNext() = Context()
}
