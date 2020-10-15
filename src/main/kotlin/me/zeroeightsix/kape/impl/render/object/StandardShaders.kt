package me.zeroeightsix.kape.impl.render.`object`

import me.zeroeightsix.kape.api.Kape
import me.zeroeightsix.kape.api.render.`object`.ShaderType.FRAGMENT_SHADER
import me.zeroeightsix.kape.api.render.`object`.ShaderType.VERTEX_SHADER

private val vertexShaderSource = Kape::class.java.getResource("/shader/to_screen.vsh").readText()
private val fragmentShaderSource = Kape::class.java.getResource("/shader/identity.fsh").readText()

val standardVertexShader by lazy {
    GlShader(vertexShaderSource, VERTEX_SHADER)
}

val standardFragmentShader by lazy {
    GlShader(fragmentShaderSource, FRAGMENT_SHADER)
}

val standardProgram by lazy {
    GlShaderProgram(standardVertexShader, standardFragmentShader)
}