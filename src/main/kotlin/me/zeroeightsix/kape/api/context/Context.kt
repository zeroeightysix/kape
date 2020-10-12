package me.zeroeightsix.kape.api.context

import me.zeroeightsix.kape.api.element.GlPrimitive
import me.zeroeightsix.kape.api.math.Vec2d
import me.zeroeightsix.kape.api.math.Vec2i

class Context(val windowState: WindowState) : Reproducible<Context> {
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

    override fun createNext() = Context(this.windowState)
}

interface WindowState {
    val mouse: Vec2d
    val charQueue: ArrayDeque<Char>
    val size: Vec2i

    fun clear()
}