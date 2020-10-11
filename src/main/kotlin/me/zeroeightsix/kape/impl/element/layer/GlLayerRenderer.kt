package me.zeroeightsix.kape.impl.element.layer

import me.zeroeightsix.kape.api.*
import me.zeroeightsix.kape.api.context.Context
import me.zeroeightsix.kape.api.element.PrimitiveType
import me.zeroeightsix.kape.api.element.Vertex
import me.zeroeightsix.kape.api.element.layer.Layer
import me.zeroeightsix.kape.api.element.layer.LayerRenderer
import me.zeroeightsix.kape.api.gl.ShaderProgram
import me.zeroeightsix.kape.impl.gl.VAO
import me.zeroeightsix.kape.impl.gl.VBO
import me.zeroeightsix.kape.impl.gl.standardProgram
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import java.util.*
import kotlin.collections.HashMap

class GlLayerRenderer(
    private val program: ShaderProgram = standardProgram,
    private val bindStack: BindStack
) : LayerRenderer<Context> {
    private val root = LayerGlNode()

    override fun render(layer: Layer<Context>, id: ID) {
        with(bindStack) {
            program.bindScoped {
                root.render(id, layer)
            }
        }
    }

    private fun LayerGlNode.render(id: ID, layer: Layer<Context>) {
        var dirty = layer.context.dirty
        val leaf = children.computeIfAbsent(id) {
            dirty = true
            LayerGlNode()
        }

        if (dirty) {
            leaf.commit(layer.context, bindStack)
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

    /**
     * Clear data, redraw from context
     */
    fun commit(context: Context, bindStack: BindStack) {
        val map = EnumMap<PrimitiveType, MutableList<Vertex>>(PrimitiveType::class.java)
        context.drawAll().map { primitive ->
            map.getOrPut(primitive.type) {
                mutableListOf()
            }.addAll(primitive.vertices)
        }

        val nodeMap = map.map { (type, vertices) ->
            val node = primitiveMap[type] ?: PrimitiveRenderedNode(type)
            val size = vertices.size * 3
            node.size = size
            val vertexArray = FloatArray(size)
            vertices.forEachIndexed { idx, vertex ->
                val i = idx * 3
                vertexArray[i] = vertex.x
                vertexArray[i + 1] = vertex.y
            }

            with (bindStack) {
                node.vao.bindScoped {
                    node.vbo.bindScoped {
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexArray, GL15.GL_STATIC_DRAW)

                        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0)
                        GL20.glEnableVertexAttribArray(0)
                    }
                }
            }

            type to node
        }.toMap()

        primitiveMap.keys.toMutableSet().also {
            it.removeAll(nodeMap.keys)
        }.forEach {
            primitiveMap[it]?.destroy()
        }

        primitiveMap.clear()
        primitiveMap.putAll(nodeMap)
    }

    fun draw() {
        this.primitiveMap.entries.forEach { (type, node) ->
            node.vao.bind()
            GL11.glDrawArrays(type.gl, 0, node.size)
        }
    }

    private class PrimitiveRenderedNode(val type: PrimitiveType) : Destroy {
        val vao = VAO()
        val vbo = VBO()

        var size = 0

        override fun destroy() = destroyAll(vao, vbo)
    }
}
