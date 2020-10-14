package me.zeroeightsix.kape.impl.render.`object`

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.render.`object`.Shader
import me.zeroeightsix.kape.api.render.`object`.ShaderProgram
import me.zeroeightsix.kape.api.util.Destroy
import org.lwjgl.opengl.GL20.*

class GlShaderProgram : ShaderProgram {
    override val bindTypeId: ID = GlShaderProgram::class

    private val pointer: Int

    @Suppress("MemberVisibilityCanBePrivate")
    fun queryLinkError(): String? {
        val status = newBoolRef()
        glGetShaderiv(pointer, GL_LINK_STATUS, status)
        return if (status()) {
            glGetProgramInfoLog(pointer)
        } else
            null
    }

    constructor(pointer: Int) {
        this.pointer = pointer
    }
    
    constructor(vararg shaders: Shader, deleteShaders: Boolean = true) {
        this.pointer = glCreateProgram()
        shaders.forEach { shader ->
            glAttachShader(pointer, shader.pointer)
        }
        glLinkProgram(pointer)

        queryLinkError()?.let { log -> error(log) }
        // No error thrown at this point - we're sure the program linked correctly!

        if (deleteShaders)
            shaders.forEach(Destroy::destroy)
    }

    private fun useProgram(pointer: Int) = glUseProgram(pointer)

    override fun bind() = useProgram(this.pointer)
    override fun resetBind() = useProgram(0)
    override fun getUniformLocation(name: String) = glGetUniformLocation(this.pointer, name)

    override fun destroy() {
        glDeleteProgram(this.pointer)
    }

}