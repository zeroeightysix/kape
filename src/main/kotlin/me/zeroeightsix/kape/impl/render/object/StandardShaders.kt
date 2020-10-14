package me.zeroeightsix.kape.impl.render.`object`

import me.zeroeightsix.kape.api.render.`object`.ShaderType.FRAGMENT_SHADER
import me.zeroeightsix.kape.api.render.`object`.ShaderType.VERTEX_SHADER

const val STD_VERTEX_SOURCE =
"""
#version 330 core
layout (location = 0) in vec2 position;
layout (location = 1) in vec4 colour;

uniform vec2 viewport;

out vec4 fragmentColour;

void main()
{
    fragmentColour = colour;
    vec4 screenPosition = vec4(2 * position / viewport - 1, 0, 1);
    vec4 yInverted = vec4(screenPosition.x, -screenPosition.y, screenPosition.zw);
    gl_Position = yInverted;
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