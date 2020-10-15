#version 330 core

in vec4 fragmentColour;
out vec4 finalFragColour;

void main()
{
    finalFragColour = fragmentColour;
}