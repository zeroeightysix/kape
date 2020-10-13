package me.zeroeightsix.kape.api.context

import me.zeroeightsix.kape.api.gl.PrimitiveType
import me.zeroeightsix.kape.api.gl.VertexFormat
import me.zeroeightsix.kape.api.math.Vec2d
import me.zeroeightsix.kape.api.math.Vec2i

private fun <T, R> Iterator<T>.map(mapper: (T) -> R) = object : Iterator<R> {
    override fun hasNext(): Boolean = this@map.hasNext()
    override fun next(): R = mapper(this@map.next())
}

class Context(val windowState: WindowState) : Reproducible<Context> {
    /**
     * Whether or not the context was modified compared to the previous iteration of contexts
     */
    var dirty = false

    fun dirty() {
        dirty = true
    }

    private val queue = ArrayDeque<() -> Triple<VertexFormat, PrimitiveType, FloatArray>>()

    fun push(supplier: () -> Triple<VertexFormat, PrimitiveType, FloatArray>) {
        queue.add(supplier)
    }

    fun drawAll(): Iterator<Triple<VertexFormat, PrimitiveType, FloatArray>> = queue.iterator().map { it() }

    override fun createNext() = Context(this.windowState)

    operator fun invoke(block: Context.() -> Unit) = block()
}

interface WindowState {
    val mouse: Vec2d
    val charQueue: ArrayDeque<Char>
    val keyQueue: ArrayDeque<KeyEvent>
    val size: Vec2i

    data class KeyEvent(val key: Int, val scancode: Int, val action: KeyAction, val mods: KeyMods)

    fun clear()
}

enum class KeyAction {
    PRESS, RELEASE, REPEAT
}

@Suppress("LeakingThis", "CanBeParameter")
open class KeyMods(
    open val shift: Boolean,
    open val control: Boolean,
    open val alt: Boolean,
    open val `super`: Boolean,
    open val capsLock: Boolean,
    open val numLock: Boolean
) {
    // Common combination getters
    val isControlAlt: Boolean = control && alt
    val isControlAltShift: Boolean = isControlAlt && shift
}