package me.zeroeightsix.kape.impl.element.layer

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.element.Context
import me.zeroeightsix.kape.api.element.PrimitiveType
import me.zeroeightsix.kape.api.element.layer.Layer
import me.zeroeightsix.kape.api.element.layer.LayerRenderer
import me.zeroeightsix.kape.impl.gl.VAO
import me.zeroeightsix.kape.impl.gl.VBO
import java.util.*
import kotlin.collections.HashMap

class GlLayerRenderer : LayerRenderer<Context> {
    private val root = LayerGlNode()

    override fun render(layer: Layer<Context>, id: ID) {
        root.render(id, layer)
    }

    private fun LayerGlNode.render(id: ID, layer: Layer<Context>) {
        val leaf = children.computeIfAbsent(id) {
            LayerGlNode()
        }

        leaf.draw()
        layer.children.forEach { (childLayerId, childLayer) ->
            leaf.render(childLayerId, childLayer)
        }

        children[id] = leaf
    }
}

private class LayerGlNode {
    val primitiveMap = EnumMap<PrimitiveType, PrimitiveRenderedNode>(PrimitiveType::class.java)

    val children = HashMap<ID, LayerGlNode>()

    fun draw() {
    }

    private class PrimitiveRenderedNode(val type: PrimitiveType) {
        val vao = VAO()
        val vbo = VBO()
    }
}
