package me.zeroeightsix.kape.impl.element.layer

import me.zeroeightsix.kape.api.*
import me.zeroeightsix.kape.api.context.Context
import me.zeroeightsix.kape.api.gl.PrimitiveType
import me.zeroeightsix.kape.api.element.layer.Layer
import me.zeroeightsix.kape.api.element.layer.LayerRenderer
import me.zeroeightsix.kape.api.gl.ShaderProgram
import me.zeroeightsix.kape.api.gl.VertexFormat
import me.zeroeightsix.kape.impl.gl.VAO
import me.zeroeightsix.kape.impl.gl.VBO
import me.zeroeightsix.kape.impl.gl.standardProgram
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20
import kotlin.collections.HashMap

class GlLayerRenderer(
    private val program: ShaderProgram = standardProgram
) : LayerRenderer<Context> {
    private val root = LayerGlNode()

    override fun render(layer: Layer<Context>, id: ID, bindStack: BindStack) {
        with(bindStack) {
            program.bindScoped {
                root.render(id, layer, bindStack)
            }
        }
    }

    private fun LayerGlNode.render(id: ID, layer: Layer<Context>, bindStack: BindStack = NoBindStack) {
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

    override fun destroy() {
        this.root.destroy()
    }
}

private class LayerGlNode : Destroy {
    val batchMap = HashMap<Pair<VertexFormat, PrimitiveType>, PrimitiveRenderBatch>()
    val children = HashMap<ID, LayerGlNode>()

    /**
     * Clear data, redraw from context
     */
    fun commit(context: Context, bindStack: BindStack) {
        val batchedFloats = HashMap<Pair<VertexFormat, PrimitiveType>, MutableList<MutableList<Float>>>()

        context.drawAll().forEach { (format, primitiveType, floats) ->
            val formatPrim = format to primitiveType
            val lists = batchedFloats.getOrPut(formatPrim) { mutableListOf() }
            // Determine what list to append to. If batchable, the first one (or the one we create if none),
            // and if not batchable always a new list.
            val appendTo = if (primitiveType.batch && lists.isNotEmpty())
                lists.first()
            else
                mutableListOf<Float>().also { lists.add(it) }
            appendTo.addAll(floats.asIterable())
        }

        batchedFloats.forEach { (formatPrim, batches) ->
            val (format, _) = formatPrim
            val batch = if (batchMap.containsKey(formatPrim)) {
                // There is already a batch: let's modify it
                val batch = batchMap[formatPrim]!!
                if (batch.nodes.size == batches.size)
                    batch
                else {
                    // We'll have to generate more nodes or destroy some
                    val nodes = batch.nodes.toMutableList()

                    // If there is an excess, destroy them
                    while (nodes.size > batches.size)
                        nodes.removeFirst().destroy()
                    // If there is a lack, create extra
                    while (nodes.size < batches.size)
                        nodes.add(PrimitiveRenderedNode())

                    PrimitiveRenderBatch(nodes.toTypedArray())
                }
            } else {
                // No batch exists, create one from scratch
                PrimitiveRenderBatch(Array(batches.size) { PrimitiveRenderedNode() })
            }

            batches.forEachIndexed { idx, floatList ->
                val floatArray = floatList.toFloatArray()
                val node = batch.nodes[idx]

                // Upload data
                with(bindStack) {
                    node.vao.bindScoped {
                        node.vbo.bindScoped {
                            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatArray, GL15.GL_STATIC_DRAW)

                            // We don't know what kind of data (just vertices? vertices and colours? nothing?)
                            // was just uploaded to the buffer, but the VertexFormat does!
                            format.setVertexAttributePointers()
                        }
                    }
                }

                node.size = floatArray.size / format.floatsPerEntry
            }

            batchMap[formatPrim] = batch
        }

        // Cleanup: what isn't in the batchedFloats map, wasn't used, and thus has to be destroyed.
        batchMap.keys.toMutableSet().apply { removeAll(batchedFloats.keys) }.forEach {
            batchMap.remove(it)?.destroy()
        }
    }

    fun draw() {
        for ((formatPrim, batch) in this.batchMap.entries) {
            val (_, type) = formatPrim
            batch.nodes.forEach { node ->
                node.vao.bind()
                GL11.glDrawArrays(type.gl, 0, node.size)
            }
        }
    }

    override fun destroy() {
        this.batchMap.values.forEach(Destroy::destroy)
    }

    private class PrimitiveRenderBatch(val nodes: Array<PrimitiveRenderedNode>) : Destroy {
        override fun destroy() = nodes.forEach(Destroy::destroy)
    }

    private class PrimitiveRenderedNode : Destroy {
        val vao = VAO()
        val vbo = VBO()

        var size = 0

        override fun destroy() = destroyAll(vao, vbo)
    }
}
