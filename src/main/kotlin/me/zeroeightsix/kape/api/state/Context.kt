package me.zeroeightsix.kape.api.state

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.render.`object`.PrimitiveType
import me.zeroeightsix.kape.api.render.`object`.VertexFormat
import kotlin.reflect.KProperty

private fun <T, R> Iterator<T>.map(mapper: (T) -> R) = object : Iterator<R> {
    override fun hasNext(): Boolean = this@map.hasNext()
    override fun next(): R = mapper(this@map.next())
}

typealias RenderEntry = Triple<Context.RenderFormat, FloatArray, IntArray?>

class Context private constructor(
    val windowState: WindowState,
    private val node: StateNode<HashMap<ID?, HashMap<ID, Any>>>,
    _dirty: Boolean = false
) :
    Reproducible<Context, ID>, Clone<Context> {
    constructor(windowState: WindowState) : this(windowState, StateNode(hashMapOf(), hashMapOf()))

    /**
     * Whether or not the context was modified compared to the previous iteration of contexts
     */
    var dirty = _dirty
        private set // Can only ever be set to true through the setDirty method

    var deepDirty = _dirty
        private set

    private val queue = ArrayDeque<() -> RenderEntry>()

    @JvmName("getStateAny")
    fun getState(ownerId: ID?, stateID: ID) = this.node.value?.get(ownerId)?.get(stateID)

    fun <T : Any> setState(ownerId: ID?, stateId: ID, value: T) =
        this.node.value?.getOrPut(ownerId) { hashMapOf() }?.put(stateId, value) != value

    fun removeState(ownerId: ID?, stateId: ID) = this.node.value?.get(ownerId)?.remove(stateId) != null

    inline fun <reified T : Any?> getProperty(
        ownerId: ID?,
        stateId: ID,
        dirtyOnChange: Boolean = false
    ) = Property(ownerId, stateId, T::class.java, dirtyOnChange)

    inline fun <reified T : Any> getPropertyDefaulted(
        ownerId: ID?,
        stateId: ID,
        dirtyOnChange: Boolean = false,
        crossinline default: () -> T,
    ) = Property(ownerId, stateId, T::class.java, dirtyOnChange).also {
        if (getState(ownerId, stateId) == null) {
            setState(ownerId, stateId, default())
            if (dirtyOnChange) setDirty()
        }
    }

    inline fun <reified T> getState(ownerId: ID?, stateId: ID) = this.getState(ownerId, stateId) as? T

    inline fun <reified T : Any> getStateOrPut(ownerId: ID?, stateId: ID, orPut: () -> T): T {
        return getState<T>(ownerId, stateId) ?: orPut().also {
            setState(ownerId, stateId, it)
        }
    }

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

    fun setDeepDirty() {
        deepDirty = true
    }

    fun dirtyIf(block: () -> Boolean) = dirtyIf(block())

    fun dirtyIf(boolean: Boolean) = if (boolean) setDirty() else Unit

    override fun clone(): Context = Context(this.windowState, this.node, this.deepDirty)

    operator fun invoke(block: Context.() -> Unit) = block()

    data class RenderFormat(val vertexFormat: VertexFormat, val primitiveType: PrimitiveType, val ebo: Boolean) {
        val batchable = this.primitiveType.batch
    }

    inner class Property<T>(
        private val ownerId: ID? = null,
        private val stateId: ID,
        private val clazz: Class<T>,
        private val dirtyOnChange: Boolean = false
    ) {
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
            if (value == null)
                removeState(ownerId, stateId)
            else if ((this@Context.node.value?.getOrPut(ownerId) { hashMapOf() }
                    ?.put(stateId, value) != value) && dirtyOnChange) {
                this@Context.setDirty()
            }
        }

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            clazz.cast(this@Context.getState(ownerId, stateId))
    }

    private class StateNode<T>(val value: T?, val children: HashMap<ID, StateNode<T>>)
}