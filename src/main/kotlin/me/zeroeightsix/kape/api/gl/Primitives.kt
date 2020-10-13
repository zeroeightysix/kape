package me.zeroeightsix.kape.api.gl

import me.zeroeightsix.kape.api.math.Vec2f
import org.lwjgl.opengl.GL11.*

typealias Vertex = Vec2f

interface Primitive {
    val vertices: Array<Vertex>
    val primitiveType: Int
}

enum class PrimitiveType(val gl: Int, val batch: Boolean = false) {
    POINTS(GL_POINTS, true),
    LINES(GL_LINES, true),
    LINE_STRIP(GL_LINE_STRIP),
    LINE_LOOP(GL_LINE_LOOP),
    POLYGON(GL_POLYGON),
    QUADS(GL_QUADS, true),
    QUAD_STRIP(GL_QUAD_STRIP),
    TRIANGLES(GL_TRIANGLES, true),
    TRIANGLE_STRIP(GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(GL_TRIANGLE_FAN);
}