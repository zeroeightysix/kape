package me.zeroeightsix.kape.impl.render.renderer

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.destroyAll
import me.zeroeightsix.kape.api.render.`object`.ShaderProgram
import me.zeroeightsix.kape.api.render.bind.BindStack
import me.zeroeightsix.kape.api.render.bind.NoBindStack
import me.zeroeightsix.kape.api.render.renderer.LayerRenderer
import me.zeroeightsix.kape.api.state.Context
import me.zeroeightsix.kape.api.state.FormatPrim
import me.zeroeightsix.kape.api.state.layer.Layer
import me.zeroeightsix.kape.api.util.Destroy
import me.zeroeightsix.kape.api.util.math.Vec2i
import me.zeroeightsix.kape.impl.render.`object`.EBO
import me.zeroeightsix.kape.impl.render.`object`.VAO
import me.zeroeightsix.kape.impl.render.`object`.VBO
import me.zeroeightsix.kape.impl.render.`object`.standardProgram
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20

class GlLayerRenderer(
    private val program: ShaderProgram = standardProgram
) : LayerRenderer<Context> {
    private val root = LayerGlNode()
    private val viewportUniform = this.program.getUniformLocation("viewport")
    private var cachedWindowSize: Vec2i? = null

    override fun render(layer: Layer<Context>, id: ID, bindStack: BindStack) {
        with(bindStack) {
            program.bindScoped {
                root.render(id, layer, bindStack)
            }
        }
    }

    private fun LayerGlNode.render(id: ID, layer: Layer<Context>, bindStack: BindStack = NoBindStack) {
        val windowSize = layer.context.windowState.size
        if (cachedWindowSize != windowSize) {
            GL20.glUniform2f(this@GlLayerRenderer.viewportUniform, windowSize.x.toFloat(), windowSize.y.toFloat())
            cachedWindowSize = windowSize
        }

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
    val batchMap = HashMap<FormatPrim, PrimitiveRenderBatch>()
    val children = HashMap<ID, LayerGlNode>()

    /**
     * Clear data, redraw from context
     */
    fun commit(context: Context, bindStack: BindStack) {
        val batchedFloats = HashMap<FormatPrim, MutableList<Pair<MutableList<Float>, IntArray?>>>()

        context.drawAll().forEach { (formatPrim, vertices, indices) ->
            val (_, primitiveType) = formatPrim
            val lists = batchedFloats.getOrPut(formatPrim) { mutableListOf() }
            // Determine what list to append to. If batchable, the first one (or the one we create if none),
            // and if not batchable always a new list.
            val appendTo = if (primitiveType.batch && lists.isNotEmpty())
                lists.first().first
            else
                mutableListOf<Float>().also { lists.add(it to indices) }
            appendTo.addAll(vertices.asIterable())
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

            batches.forEachIndexed { idx, (floatList, indices) ->
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

                    // No indices but an EBO exists, destroy the EBO
                    if (indices == null && node.ebo != null) {
                        println("EBO destroyed")
                        node.ebo!!.destroy()
                        node.ebo = null
                    }

                    if (indices != null) {
                        // If we need an EBO but there is none, create one
                        if (node.ebo == null)
                            node.ebo = EBO().also { println("EBO made") }

                        node.eboSize = indices.size
                        // We're sure there is an EBO now, so we can bind it (and null-assert)
                        node.ebo!!.bindScoped {
                            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW)
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
                val ebo = node.ebo
                if (ebo != null) {
                    ebo.bind()
                    GL11.glDrawElements(type.gl, node.eboSize, GL11.GL_UNSIGNED_INT, 0)
                } else {
                    GL11.glDrawArrays(type.gl, 0, node.size)
                }
            }
        }
    }

    override fun destroy() {
        this.batchMap.values.forEach(Destroy::destroy)
    }

    private class PrimitiveRenderBatch(val nodes: Array<PrimitiveRenderedNode>) : Destroy {
        override fun destroy() = nodes.forEach(Destroy::destroy)
    }

    private class PrimitiveRenderedNode(var ebo: EBO? = null, var eboSize: Int = 0) : Destroy {
        val vao = VAO()
        val vbo = VBO()

        var size = 0

        override fun destroy() = destroyAll(vao, vbo)
    }
}
