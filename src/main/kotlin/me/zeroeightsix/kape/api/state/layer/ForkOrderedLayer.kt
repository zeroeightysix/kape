package me.zeroeightsix.kape.api.state.layer

import me.zeroeightsix.kape.api.ID

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