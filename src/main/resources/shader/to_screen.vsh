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