package me.zeroeightsix.kape.impl.gl

import me.zeroeightsix.kape.api.gl.Shader
import me.zeroeightsix.kape.api.gl.ShaderType
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.*

class GlShader: Shader {

    override val pointer: Int

    @Suppress("MemberVisibilityCanBePrivate")
    fun queryCompileError(): String? {
        val status = newBoolRef()
        glGetShaderiv(pointer, GL_COMPILE_STATUS, status)
        return if (!status()) {
            glGetShaderInfoLog(pointer)
        } else
            null
    }
    
    constructor(pointer: Int) {
        this.pointer = pointer
    }

    constructor(sourceCode: String, type: ShaderType) {
        this.pointer = glCreateShader(type.glType)
        glShaderSource(pointer, sourceCode)
        glCompileShader(pointer)

        queryCompileError()?.let { log -> error(log) }
        // No error thrown at this point - we're sure the shader compiled!
    }

    override fun destroy() {
        glDeleteShader(this.pointer)
    }
    
}