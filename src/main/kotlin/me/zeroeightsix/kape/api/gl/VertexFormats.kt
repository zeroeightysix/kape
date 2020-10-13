package me.zeroeightsix.kape.api.gl

import me.zeroeightsix.kape.api.*
import me.zeroeightsix.kape.api.context.Context
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20

interface VertexFormat {
    val floatsPerEntry: Int

    fun setVertexAttributePointers()
}

typealias PrimColour = Pair<Vertex, Colour>

private fun FloatArray.shoveVertex(at: Int, vertex: Vertex) {
    this[at] = vertex.x
    this[at + 1] = vertex.y
}

@Suppress("EXPERIMENTAL_API_USAGE")
private fun FloatArray.shoveColour(at: Int, colour: Colour) {
    val r = colour.r.toFloat() / 255f
    val g = colour.g.toFloat() / 255f
    val b = colour.b.toFloat() / 255f
    val a = colour.a.toFloat() / 255f

    this[at] = r
    this[at + 1] = g
    this[at + 2] = b
    this[at + 3] = a
}

object VertexColour : VertexFormat {
    private const val vertexCount = 2
    private const val colourCount = 4
    override val floatsPerEntry = vertexCount + colourCount

    private const val vertexStride = Float.SIZE_BYTES * vertexCount
    private const val colourStride = Float.SIZE_BYTES * colourCount
    private const val stride: Int = vertexStride + colourStride

    fun Context.push(type: PrimitiveType, vararg vAttributes: PrimColour) {
        push {
            val floats = FloatArray(floatsPerEntry * vAttributes.size)
            vAttributes.forEachIndexed { primColIdx, (vex, col) ->
                val zeroIdx = primColIdx * floatsPerEntry
                floats.shoveVertex(zeroIdx, vex)
                floats.shoveColour(zeroIdx + vertexCount, col)
            }
            Triple(this@VertexColour, type, floats)
        }
    }

    override fun setVertexAttributePointers() {
        fun vertexAttribPointer(index: Int, size: Int, type: Int = GL11.GL_FLOAT, normalised: Boolean = false, stride: Int, pointer: Long) {
            GL20.glVertexAttribPointer(index, size, type, normalised, stride, pointer)
            GL20.glEnableVertexAttribArray(index)
        }
        vertexAttribPointer(0, vertexCount, stride = this.stride, pointer = 0)
        vertexAttribPointer(1, colourCount, stride = this.stride, pointer = this.vertexStride.toLong())
    }

    operator fun Vertex.rem(colour: Colour): PrimColour = this to colour
}

operator fun <T : VertexFormat> T.invoke(block: T.() -> Unit) = block()