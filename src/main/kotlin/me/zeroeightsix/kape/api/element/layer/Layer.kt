package me.zeroeightsix.kape.api.element.layer

import me.zeroeightsix.kape.api.ID

typealias LayerMap = LinkedHashMap<ID, Layer>

interface Layer {
    
    val parent: Layer?
    val children: LayerMap

    /**
     * Creates a new [Layer] with `this` as parent, and adds it to this layer's children.
     */
    fun fork(child: Layer, id: ID = child)

    fun bringToFront(id: ID) {
        this.children[id] = this.children.remove(id) ?: return
    }
    
}

/**
 * A [Layer] with children ordered in the order they were forked in.
 */
open class ForkOrderedLayer(override val parent: Layer? = null) : Layer {

    override val children: LayerMap = LayerMap()

    override fun fork(child: Layer, id: ID) {
        assert(child.parent === this) {
            "Tried to fork layer with child with a parent that is not this layer"
        }
        children.computeIfPresent(id) { _, layer ->
            JoinedLayer(layer, child)
        }
    }

}

internal class JoinedLayer(private val first: Layer, private val second: Layer) : ForkOrderedLayer() {

    init {
        assert(first.parent == second.parent) {
            "Attempt to join layers with differing parents"
        }
    }

    override val parent: Layer? = first.parent
    override val children: LayerMap = this.first.children.toMutableMap().also { it.putAll(this.second.children) } as LayerMap

}