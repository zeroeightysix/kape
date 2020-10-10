package me.zeroeightsix.kape.api.element

import me.zeroeightsix.kape.api.math.Vec2f
import org.lwjgl.opengl.GL11.*

typealias Vertex = Vec2f

interface Primitive {
    val vertices: Array<Vertex>
    val primitiveType: Int
}

enum class PrimitiveType(val gl: Int) {
    POINTS(GL_POINTS),
    LINES(GL_LINES),
    LINE_STRIP(GL_LINE_STRIP),
    LINE_LOOP(GL_LINE_LOOP),
    POLYGON(GL_POLYGON),
    QUADS(GL_QUADS),
    QUAD_STRIP(GL_QUAD_STRIP),
    TRIANGLES(GL_TRIANGLES),
    TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(GL_TRIANGLE_FAN);
}

open class GlPrimitive(private val type: PrimitiveType, override val vertices: Array<Vertex>) : Primitive {
    override val primitiveType: Int = this.type.gl
}