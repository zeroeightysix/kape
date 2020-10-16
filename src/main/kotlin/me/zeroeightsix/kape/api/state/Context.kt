package me.zeroeightsix.kape.api.state

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.render.`object`.PrimitiveType
import me.zeroeightsix.kape.api.render.`object`.VertexFormat

private fun <T, R> Iterator<T>.map(mapper: (T) -> R) = object : Iterator<R> {
    override fun hasNext(): Boolean = this@map.hasNext()
    override fun next(): R = mapper(this@map.next())
}

typealias FormatPrim = Pair<VertexFormat, PrimitiveType>
typealias RenderEntry = Triple<FormatPrim, FloatArray, IntArray?>

class Context private constructor(
    val windowState: WindowState,
    private val node: StateNode<HashMap<ID, HashMap<ID, Any>>>
) :
    Reproducible<Context, ID>, Clone<Context> {
    constructor(windowState: WindowState) : this(windowState, StateNode(hashMapOf(), hashMapOf()))

    /**
     * Whether or not the context was modified compared to the previous iteration of contexts
     */
    var dirty = false

    private val queue = ArrayDeque<() -> RenderEntry>()

    @JvmName("getStateAny")
    fun getState(ownerId: ID, stateID: ID) = this.node.value?.get(ownerId)?.get(stateID)

    fun <T : Any> setState(ownerId: ID, stateId: ID, value: T) =
        this.node.value?.getOrPut(ownerId) { hashMapOf() }?.set(stateId, value)

    inline fun <reified T> getState(ownerId: ID, stateId: ID) = this.getState(ownerId, stateId) as? T

    override fun createNext(requirements: ID) = Context(
        this.windowState,
        this.node.children.getOrPut(requirements) { StateNode(hashMapOf(), hashMapOf()) }
    )

    fun push(supplier: () -> RenderEntry) {
        queue.add(supplier)
    }

    fun drawAll(): Iterator<RenderEntry> = queue.iterator().map { it() }

    fun setDirty() {
        dirty = true
    }

    override fun clone(): Context = Context(this.windowState, this.node)

    operator fun invoke(block: Context.() -> Unit) = block()

    private class StateNode<T>(val value: T?, val children: HashMap<ID, StateNode<T>>)
}