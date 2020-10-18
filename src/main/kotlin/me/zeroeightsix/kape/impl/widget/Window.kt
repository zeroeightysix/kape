@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")

package me.zeroeightsix.kape.impl.widget

import me.zeroeightsix.kape.api.ID
import me.zeroeightsix.kape.api.render.`object`.PrimitiveType.TRIANGLES
import me.zeroeightsix.kape.api.render.`object`.VertexColour
import me.zeroeightsix.kape.api.render.`object`.invoke
import me.zeroeightsix.kape.api.state.Context
import me.zeroeightsix.kape.api.state.layer.Layer
import me.zeroeightsix.kape.api.util.Colour
import me.zeroeightsix.kape.api.util.blue
import me.zeroeightsix.kape.api.util.copy
import me.zeroeightsix.kape.api.util.math.*

private const val transparentGrey: Colour = 0x111111EEu

private val String.private
    get() = "kape:__$this"

fun Layer<Context>.window(title: String = "Kape window", id: ID = title) {
    val ctx = this.context

    val windowSize = ctx.getState<Vec2f>(id, "size".private) ?: Vec2f(100f).also {
        // Immediately set it if it was not present
        ctx.setState(id, "size", it)
    }

    val windowPosition = ctx.getState<Vec2f>(id, "position".private) ?: Vec2f(10f).also {
        ctx.setState(id, "position", it)
    }

    ctx {
        VertexColour {
            push(
                TRIANGLES,
                windowPosition.offsets(
                    inclusive = true,   // 0
                    windowSize,         // 1
                    windowSize.justX,   // 2
                    windowSize.justY    // 3
                ),
                intArrayOf(0, 2, 1, 0, 1, 3),
                colour = transparentGrey
            )
        }
    }

    // owner == null: 'global layer' state
    val currentResizing = ctx.getState<ID>(null, "resizingWindow".private)
    val resizeKnobSize =
        20f // todo: should this be configurable? probably, but do it through 'style' (à la dear imgui), or per window?
    val resizeHovered = ctx.getState(id, "resizeHovered") != null
    if (currentResizing == id || currentResizing == null) {
        val (x, y) = ctx.windowState.mouse

        var colour = blue.copy(a = 128u)

        fun checkHovered(): Boolean {
            if (x < windowPosition.x + windowSize.x && y < windowPosition.y + windowSize.y) {
                // Trick: to test if a point is within a right triangle with equal opposite and adjacent side,
                // the sum of the distance to the opposite side and adjacent side must be equal or smaller than the length
                // of that equal side.
                if ((windowPosition.x + windowSize.x - x) + (windowPosition.y + windowSize.y - y) <= resizeKnobSize) {
                    colour = colour.copy(a = 255u)
                    if (ctx.setState(id, "resizeHovered", Unit)) {
                        ctx.setDirty()
                    }
                    return true
                }
            }
            return false
        }

        // If the resize knob is not hovered, and removing the hovered state did something (i.e. last frame it was
        // hovered), set context dirty.
        if (!checkHovered() && resizeHovered && ctx.removeState(id, "resizeHovered")) {
            ctx.setDirty()
        }

        ctx {
            VertexColour {
                push(
                    TRIANGLES,
                    windowPosition.offsets(
                        inclusive = false,
                        windowSize,
                        windowSize - Vec2f(0f, resizeKnobSize),
                        windowSize - Vec2f(resizeKnobSize, 0f)
                    ),
                    colour = colour
                )
            }
        }

        if (ctx.setState(id, "resizeShown", true))
            ctx.setDirty()
    } else {
        if (ctx.setState(id, "resizeShown", false))
            ctx.setDirty()
    }
}