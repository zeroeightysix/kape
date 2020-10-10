package me.zeroeightsix.kape.api.gl

import me.zeroeightsix.kape.api.Bind
import me.zeroeightsix.kape.api.Destroy
import org.lwjgl.opengl.GL32

interface Shader : Destroy {
    val pointer: Int
}

enum class ShaderType(val glType: Int) {
    GEOMETRY_SHADER(GL32.GL_GEOMETRY_SHADER),
    VERTEX_SHADER(GL32.GL_VERTEX_SHADER),
    FRAGMENT_SHADER(GL32.GL_FRAGMENT_SHADER)
}

interface ShaderProgram : Destroy, Bind