package me.zeroeightsix.kape.api.render.`object`

import me.zeroeightsix.kape.api.render.bind.Bind
import me.zeroeightsix.kape.api.util.Destroy
import org.lwjgl.opengl.GL32

typealias UniformLocation = Int

interface Shader : Destroy {
    val pointer: Int
}

enum class ShaderType(val glType: Int) {
    GEOMETRY_SHADER(GL32.GL_GEOMETRY_SHADER),
    VERTEX_SHADER(GL32.GL_VERTEX_SHADER),
    FRAGMENT_SHADER(GL32.GL_FRAGMENT_SHADER)
}

interface ShaderProgram : Destroy, Bind {
    fun getUniformLocation(name: String): UniformLocation
}