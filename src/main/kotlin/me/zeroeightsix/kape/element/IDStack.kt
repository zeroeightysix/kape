package me.zeroeightsix.kape.element

// The JVM provides a hashCode for `Any`, so we're allowed to use any object as ID.
typealias ID = Any

typealias IDMap<T> = MutableMap<ID, T>

interface HasIDStack {

    /**
     * The ID stack. The top element is expected to be in the *front* of the queue.
     */
    val idStack: ArrayDeque<ID>

    fun pushID(id: ID) = idStack.addFirst(id)
    fun popID() = idStack.removeFirst()
    
    fun withID(id: ID, block: () -> Unit) {
        pushID(id)
        block()
        popID()
    }
    
    val currentID
        get() = idStack.first()
    
}