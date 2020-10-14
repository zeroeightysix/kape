package me.zeroeightsix.kape.api.state

import me.zeroeightsix.kape.api.render.`object`.PrimitiveType
import me.zeroeightsix.kape.api.render.`object`.VertexFormat

private fun <T, R> Iterator<T>.map(mapper: (T) -> R) = object : Iterator<R> {
    override fun hasNext(): Boolean = this@map.hasNext()
    override fun next(): R = mapper(this@map.next())
}

typealias FormatPrim = Pair<VertexFormat, PrimitiveType>
typealias RenderEntry = Triple<FormatPrim, FloatArray, IntArray?>

class Context(val windowState: WindowState) : Reproducible<Context> {
    /**
     * Whether or not the context was modified compared to the previous iteration of contexts
     */
    var dirty = false

    fun dirty() {
        dirty = true
    }

    private val queue = ArrayDeque<() -> RenderEntry>()

    fun push(supplier: () -> RenderEntry) {
        queue.add(supplier)
    }

    fun drawAll(): Iterator<RenderEntry> = queue.iterator().map { it() }

    override fun createNext() = Context(this.windowState)

    operator fun invoke(block: Context.() -> Unit) = block()
}