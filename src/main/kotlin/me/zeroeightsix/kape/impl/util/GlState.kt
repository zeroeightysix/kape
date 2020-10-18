package me.zeroeightsix.kape.impl.util

import org.lwjgl.opengl.GL11.*

class GlState private constructor(
    val blend: Boolean,
    val blendSrc: Int,
    val blendDst: Int
) {
    /**
     * Create a [GlState] from the current GL state
     */
    constructor() : this(
        blend = glGetBoolean(GL_BLEND),
        blendSrc = glGetInteger(GL_BLEND_SRC),
        blendDst = glGetInteger(GL_BLEND_DST)
    )

    private fun setState(flag: Int, state: Boolean) {
        if (state) {
            glEnable(flag)
        } else {
            glDisable(flag)
        }
    }

    /**
     * Sets all GL flags to match those stored in this [GlState]
     */
    fun apply() {
        setState(GL_BLEND, this.blend)
        glBlendFunc(this.blendSrc, this.blendDst)
    }

    companion object {
        val kapeState = GlState(
            true,
            GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
        )
    }
}