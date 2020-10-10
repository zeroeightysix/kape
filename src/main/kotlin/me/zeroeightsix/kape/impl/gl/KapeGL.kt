package me.zeroeightsix.kape.impl.gl

import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11

object KapeGL {

    fun setUp() {
        GL.createCapabilities()

        GL11.glClearColor(1f, 1f, 1f, 0f)
    }

    fun clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    }

}