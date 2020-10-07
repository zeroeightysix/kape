package me.zeroeightsix.kape.element

// The JVM provides a `equals` implementation for Any, so we can assume any type is allowed for IDs.
// This means that the user can choose if they want to provide e.g. objects to the data types their GUI represents
// as IDs, titles, or any other data.
// Kape, by default, uses a `LinkedHashMap` for its ID map - thus, two IDs are identical if, and only if, their
// `hashCode` implementation produces the same hash.
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