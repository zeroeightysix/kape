package me.zeroeightsix.kape.impl.gl

import me.zeroeightsix.kape.api.gl.Buffer
import org.lwjgl.opengl.GL15.*

typealias VBO = VertexBufferObject

abstract class GlBuffer : Buffer {

    abstract val type: Int
    private val pointer: Int
    private var bound = false

    constructor(pointer: Int) {
        this.pointer = pointer
    }
    
    constructor() {
        this.pointer = glGenBuffers()
    }

    private fun bind(pointer: Int) = glBindBuffer(this.type, pointer)

    override fun bind() {
        bind(this.pointer)
        this.bound = true
    }

    override fun unBind() {
        bind(0)
        this.bound = false
    }

    override fun destroy() {
        glDeleteBuffers(this.pointer)
    }

}

class VertexBufferObject : GlBuffer() {
    override val type: Int = GL_ARRAY_BUFFER
}