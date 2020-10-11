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
    val primitiveMap = EnumMap<PrimitiveType, PrimitiveRenderBatch>(PrimitiveType::class.java)

    val children = HashMap<ID, LayerGlNode>()

    /**
     * Clear data, redraw from context
     */
    fun commit(context: Context, bindStack: BindStack) {
        // The goal: redraw what has to be redrawn, and reuse as much resources as possible.
        // map of primitive type -> batch of list of vertices
        // if primitive.batch, the first the outer mutable list should always be of size 1.
        val vexMap = EnumMap<PrimitiveType, MutableList<MutableList<Vertex>>>(PrimitiveType::class.java)
        context.drawAll().forEach { primitive ->
            val type = primitive.type
            val batches = vexMap.getOrPut(type) { mutableListOf() }
            val vexList = if (type.batch) {
                batches.firstOrNull() ?: mutableListOf<Vertex>().also { batches.add(it) }
            } else {
                mutableListOf<Vertex>().also { vexList -> batches.add(vexList) }
            }

            vexList.addAll(primitive.vertices)
        }
        // Immutable version: also convert Vertex -> 3*float
        val fVexMap = EnumMap<PrimitiveType, List<FloatArray>>(PrimitiveType::class.java)
        vexMap.forEach { (type, batches) ->
            val listOfFloats = batches.map { vertices ->
                val size = vertices.size * 3 // 3 coordinates per vertex
                val array = FloatArray(size)
                vertices.forEachIndexed { idx, vertex ->
                    val coordinateIdx = idx * 3
                    array[coordinateIdx] = vertex.x
                    array[coordinateIdx + 1] = vertex.y
                    // array[idx + 2] is the z coordinate, which always stays 0f
                }
                array
            }
            fVexMap[type] = listOfFloats
        }

        // Now we have something we can start uploading to VBOs.
        val newPrimMap = fVexMap.map { (newPrimType, batches) ->
            val newBatchSize = batches.size
            val renderBatch = primitiveMap.compute(newPrimType) { type, existingBatch ->
                existingBatch?.let { batch ->
                    // Perfect: we don't have to create/destroy any nodes, as we can reuse exactly all.
                    if (batch.nodes.size == newBatchSize)
                        return@let batch

                    val nodes = batch.nodes.toMutableList()
                    // Refit batch
                    while (nodes.size > newBatchSize) {
                        // There are too many nodes: we'll need to destroy some
                        nodes.removeFirst().destroy()
                    }
                    while (nodes.size < newBatchSize) {
                        // There aren't enough nodes: we'll have to create some
                        nodes.add(PrimitiveRenderedNode(type))
                    }

                    PrimitiveRenderBatch(type, nodes.toTypedArray())
                } ?: run {
                    // No batch yet: generate nodes & create batch
                    PrimitiveRenderBatch(type, Array(newBatchSize) { PrimitiveRenderedNode(type) })
                }
            }!!

            batches.forEachIndexed { idx, floats ->
                val node = renderBatch.nodes[idx]
                // Upload data
                with(bindStack) {
                    node.vao.bindScoped {
                        node.vbo.bindScoped {
                            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floats, GL15.GL_STATIC_DRAW)

                            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0)
                            GL20.glEnableVertexAttribArray(0)
                        }
                    }
                }

                // Update the node size
                node.size = floats.size / 3
            }

            newPrimType to renderBatch
        }.toMap()

        // All done: now clean up entries in the primitiveMap that weren't modified (and thus, need to be omitted)
        primitiveMap.keys.toMutableSet().apply { removeAll(newPrimMap.keys) }.forEach {
            primitiveMap[it]?.destroy()
        }

        primitiveMap.clear()
        primitiveMap.putAll(newPrimMap)
    }

    fun draw() {
        this.primitiveMap.entries.forEach { (type, batch) ->
            batch.nodes.forEach { node ->
                node.vao.bind()
                GL11.glDrawArrays(type.gl, 0, node.size)
            }
        }
    }

    private class PrimitiveRenderBatch(val type: PrimitiveType, val nodes: Array<PrimitiveRenderedNode>) : Destroy {
        override fun destroy() = nodes.forEach(Destroy::destroy)
    }

    private class PrimitiveRenderedNode(val type: PrimitiveType) : Destroy {
        val vao = VAO()
        val vbo = VBO()

        var size = 0

        override fun destroy() = destroyAll(vao, vbo)
    }
}
