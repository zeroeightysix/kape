package me.zeroeightsix.kape.impl.gl

import me.zeroeightsix.kape.api.gl.ShaderType.FRAGMENT_SHADER
import me.zeroeightsix.kape.api.gl.ShaderType.VERTEX_SHADER

const val STD_VERTEX_SOURCE =
"""
#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec4 vertexColour;

out vec4 fragmentColour;

void main()
{
    fragmentColour = vertexColour;
    gl_Position = vec4(aPos, 0.0, 1.0);
}
"""

const val STD_FRAGMENT_SOURCE =
"""
#version 330 core

in vec4 fragmentColour;
out vec4 finalFragColour;

void main()
{
    finalFragColour = fragmentColour;
}
"""

val standardVertexShader by lazy {
    GlShader(STD_VERTEX_SOURCE, VERTEX_SHADER)
}

val standardFragmentShader by lazy {
    GlShader(STD_FRAGMENT_SOURCE, FRAGMENT_SHADER)
}

val standardProgram by lazy {
    GlShaderProgram(standardVertexShader, standardFragmentShader)
}