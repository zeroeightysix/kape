package me.zeroeightsix.kape.api.element.layer

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

/**
 * A [Layer] with children ordered in the order they were forked in.
 */
abstract class ForkOrderedLayer<P>(override val parent: Layer<P>? = null) : Layer<P> {

    override val children: LayerMap<P> = LayerMap()

    override fun fork(child: Layer<P>, id: ID) {
        assert(child.parent === this) {
            "Tried to fork layer with child with a parent that is not this layer"
        }
        if (children[id] != null)
            error("Can not fork with an ID that already has a layer attached")
        children[id] = child
    }

}