package me.zeroeightsix.kape.impl.render.renderer

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.destroyAll
import me.zeroeightsix.kape.api.render.`object`.PrimitiveType
import me.zeroeightsix.kape.api.render.`object`.ShaderProgram
import me.zeroeightsix.kape.api.render.`object`.VertexFormat
import me.zeroeightsix.kape.api.render.bind.BindStack
import me.zeroeightsix.kape.api.render.bind.NoBindStack
import me.zeroeightsix.kape.api.render.renderer.LayerRenderer
import me.zeroeightsix.kape.api.state.Context
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

        leaf.draw(bindStack)
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
    // Used for rendering. Contains all nodes & batches, in order.
    private var drawables = arrayOf<Drawable>()

    val children = HashMap<ID, LayerGlNode>()

    /**
     * Clear data, redraw from context
     */
    fun commit(context: Context, bindStack: BindStack) {
        this.drawables.forEach(Destroy::destroy)

        val intermediate =
            LinkedHashMap<Context.RenderFormat, MutableList<Pair<MutableList<Float>, MutableList<Int>>>>()

        context.drawAll().forEach { (format, floats, indices) ->
            if (format.ebo && indices == null) error("Can not render format with EBO enabled without supplying indices")
            // If there is no entry yet in the intermediate map, create one:
            val list = intermediate.getOrPut(format) { mutableListOf() }

            val (vAttributes, indexList) = if (format.batchable) {
                list.firstOrNull() ?: (mutableListOf<Float>() to mutableListOf<Int>()).apply { list.add(this) }
            } else {
                (mutableListOf<Float>() to mutableListOf<Int>()).apply { list.add(this) }
            }

            vAttributes.addAll(floats.asIterable())
            indices?.let {
                indexList.addAll(it.asIterable())
            }
        }

        this.drawables = intermediate.mapNotNull { (format, batches) ->
            fun createNode(floats: FloatArray, indices: IntArray): Node {
                val node = if (indices.isNotEmpty())
                    EBONode(format.primitiveType, indices.size)
                else
                    Node(format.primitiveType, floats.size / format.vertexFormat.floatsPerEntry)

                node.upload(format.vertexFormat, bindStack, floats, indices)
                return node
            }

            when (batches.size) {
                0 -> return@mapNotNull null
                1 -> {
                    val (floats, indices) = batches[0]
                    createNode(floats.toFloatArray(), indices.toIntArray())
                }
                else -> {
                    Batch(batches.map { (floats, indices) ->
                        createNode(floats.toFloatArray(), indices.toIntArray())
                    }.toTypedArray())
                }
            }
        }.toTypedArray()
    }

    fun draw(bindStack: BindStack) {
        this.drawables.forEach {
            it.draw(bindStack)
        }
    }

    override fun destroy() {
        destroyAll(*this.drawables)
    }

    private interface Drawable : Destroy {
        fun draw(bindStack: BindStack)
    }

    private class Batch(private val nodes: Array<Drawable>) : Drawable {
        override fun draw(bindStack: BindStack) {
            nodes.forEach { it.draw(bindStack) }
        }

        override fun destroy() {
            nodes.forEach(Destroy::destroy)
        }
    }

    private open class Node(protected val type: PrimitiveType, open var size: Int = 0) : Drawable {
        protected val vao = VAO()
        protected val vbo = VBO()

        protected open fun drawInternal() {
            GL11.glDrawArrays(this.type.gl, 0, this.size)
        }

        override fun draw(bindStack: BindStack) {
            with(bindStack) {
                vao.bindScoped {
                    drawInternal()
                }
            }
        }

        open fun upload(format: VertexFormat, bindStack: BindStack, data: FloatArray, indices: IntArray) {
            with(bindStack) {
                vao.bindScoped {
                    vbo.bindScoped {
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW)
                        format.setVertexAttributePointers()
                    }
                }
            }
        }

        override fun destroy() = destroyAll(vao, vbo)
    }

    private class EBONode(type: PrimitiveType, override var size: Int = 0) : Node(type, size) {
        val ebo = EBO()

        override fun upload(format: VertexFormat, bindStack: BindStack, data: FloatArray, indices: IntArray) {
            with(bindStack) {
                vao.bindScoped {
                    vbo.bindScoped {
                        ebo.bindScoped {
                            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW)
                            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW)
                            format.setVertexAttributePointers()
                        }
                    }
                }
            }
        }

        override fun drawInternal() {
            ebo.bind()
            GL15.glDrawElements(this.type.gl, this.size, GL11.GL_UNSIGNED_INT, 0)
        }

        override fun destroy() {
            super.destroy()
            this.ebo.destroy()
        }
    }
}
