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
import me.zeroeightsix.kape.impl.util.window.GlfwWindow.Action.PRESS
import me.zeroeightsix.kape.impl.util.window.GlfwWindow.Action.RELEASE
import kotlin.math.min

private const val transparentGrey: Colour = 0x111111EEu

private val sizeKey = "size".private
private val positionKey = "position".private
private val resizingWindowKey = "isResizingWindow".private
private val resizeHoveredKey = "resizeHovered".private
private val resizeShownKey = "resizeShown".private

private val String.private: ID
    get() = "kape:__$this"

private operator fun <A, B> Pair<A, B>?.component1() = this?.first
private operator fun <A, B> Pair<A, B>?.component2() = this?.second

fun Layer<Context>.window(title: String = "Kape window", id: ID = title) {
    val ctx = this.context

    val windowSize = ctx.getStateOrPut(id, sizeKey) { Vec2f(100f) }
    val windowPosition = ctx.getStateOrPut(id, positionKey) { Vec2f(10f) }

    // Draw window frame
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

    val (currentResizing, resizeAnchor) = ctx.getState<Pair<ID, Vec2f>?>(null, resizingWindowKey)

    // todo: should this be configurable? probably, but do it through 'style' (à la dear imgui), or per window?
    val resizeKnobSize = min(min(windowSize.x, windowSize.y), 20f)
    val resizeHovered = ctx.getState(id, resizeHoveredKey) != null
    val resizeShown = if (currentResizing == null || currentResizing == id) {
        val (x, y) = ctx.windowState.mouse

        var colour = blue.copy(a = 128u)

        fun checkHovered(): Boolean {
            if (x < windowPosition.x + windowSize.x && y < windowPosition.y + windowSize.y) {
                // Trick: to test if a point is within a right triangle with equal opposite and adjacent side,
                // the sum of the distance to the opposite side and adjacent side must be equal or smaller than the length
                // of that equal side.
                if ((windowPosition.x + windowSize.x - x) + (windowPosition.y + windowSize.y - y) <= resizeKnobSize) {
                    colour = colour.copy(a = 255u)
                    if (ctx.setState(id, resizeHoveredKey, Unit)) {
                        ctx.setDirty()
                    }
                    return true
                }
            }
            return false
        }

        // If the resize knob is not hovered, and removing the hovered state did something (i.e. last frame it was
        // hovered), set context dirty.
        if (!checkHovered() && resizeHovered && ctx.removeState(id, resizeHoveredKey)) {
            ctx.setDirty()
        }

        // Draw the resize knob
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

        true
    } else {
        false
    }

    if (ctx.setState(id, resizeShownKey, resizeShown))
        ctx.setDirty()

    if (currentResizing == id && ctx.windowState.mouseDelta != Vec2d(0.0)) {
        // Currently resizing this window and mouse was moved
        if (ctx.windowState.mouseQueue.lastOrNull()?.let {
                if (it.button == 0 && it.action == RELEASE) return@let false
                true
            } == false) {
            ctx.removeState(null, resizingWindowKey)
            ctx.windowState.mouseQueue.removeLast()
        } else {
            val newSize = (ctx.windowState.mouse.toVec2f() - windowPosition) + resizeAnchor!!
            ctx.setState(id, sizeKey, newSize)
            ctx.setDirty()
        }
    } else if (currentResizing == null && resizeHovered) {
        ctx.windowState.mouseQueue.lastOrNull()?.let {
            if (it.button == 0 && it.action == PRESS) {
                val distFromKnob = (windowPosition + windowSize) - ctx.windowState.mouse.toVec2f()
                ctx.setState(null, resizingWindowKey, id to distFromKnob)
                ctx.windowState.mouseQueue.removeLast()
            }
        }
    }

}