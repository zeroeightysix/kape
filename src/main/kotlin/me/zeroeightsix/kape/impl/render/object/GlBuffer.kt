package me.zeroeightsix.kape.impl.render.`object`

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.render.`object`.Buffer
import org.lwjgl.opengl.GL30.*

typealias VBO = VertexBufferObject
typealias VAO = VertexArrayObject

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

    override fun resetBind() {
        bind(0)
        this.bound = false
    }

    override fun destroy() {
        glDeleteBuffers(this.pointer)
    }

}

class VertexBufferObject : GlBuffer() {
    override val bindTypeId: ID = VertexBufferObject::class

    override val type: Int = GL_ARRAY_BUFFER
}

class VertexArrayObject : Buffer {
    override val bindTypeId: ID = VertexArrayObject::class

    private val pointer: Int
    private var bound = false

    constructor(pointer: Int) {
        this.pointer = pointer
    }
    
    constructor() {
        this.pointer = glGenVertexArrays()
    }

    private fun bind(pointer: Int) = glBindVertexArray(pointer)

    override fun bind() {
        bind(this.pointer)
        this.bound = true
    }

    override fun resetBind() {
        bind(0)
        this.bound = false
    }

    override fun destroy() {
        glDeleteVertexArrays(this.pointer)
    }
}