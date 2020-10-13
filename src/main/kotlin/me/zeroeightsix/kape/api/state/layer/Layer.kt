package me.zeroeightsix.kape.api.state.layer

import me.zeroeightsix.kape.api.ID

typealias LayerMap<P> = LinkedHashMap<ID, Layer<P>>

interface Layer<P> {
    
    val parent: Layer<P>?
    val children: LayerMap<P>

    val context: P

    /**
     * Creates a new [Layer] with `this` as parent, and adds it to this layer's children.
     */
    fun fork(child: Layer<P>, id: ID = child)

    fun bringToFront(id: ID) {
        this.children[id] = this.children.remove(id) ?: return
    }
    
}