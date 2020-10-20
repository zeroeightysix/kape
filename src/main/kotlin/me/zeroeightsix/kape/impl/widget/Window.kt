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
import me.zeroeightsix.kape.impl.util.window.GlfwWindow
import me.zeroeightsix.kape.impl.util.window.GlfwWindow.Action.PRESS
import kotlin.math.min

private const val transparentGrey: Colour = 0x111111EEu

private val sizeKey = "size".private
private val positionKey = "position".private
private val resizingWindowKey = "isResizingWindow".private
private val resizeActiveKey = "resizeActive".private
private val resizeVisibleKey = "resizeVisible".private
private val resizeHandleSizeKey = "resizeHandleSize".private

private val String.private: ID
    get() = "kape:__$this"

private operator fun <A, B> Pair<A, B>?.component1() = this?.first
private operator fun <A, B> Pair<A, B>?.component2() = this?.second

/**
 * Enact the resizing behaviour for a window. Will mutate the context to match new state, but doesn't draw anything.
 *
 * @return `true` if the resize handle should be drawn
 */
private fun resizeBehaviour(ctx: Context, id: ID): Boolean {
    var (currentResizing, resizeAnchor) = ctx.getState<Pair<ID, Vec2f>?>(null, resizingWindowKey)

    var size by ctx.getPropertyDefaulted(id, sizeKey, true) { Vec2f(100f) }
    val position by ctx.getPropertyDefaulted(id, positionKey, true) { Vec2f(10f) }
    var bottomRight = position + size
    val resizeHandleSize by ctx.getPropertyDefaulted(id, resizeHandleSizeKey, true) {
        // todo: should this be configurable? probably, but do it through 'style' (à la dear imgui), or per window?
        min(min(size.x, size.y), 20f)
    }

    val mouse = ctx.windowState.mouse.toVec2f()

    val resizeHandleVisible = (currentResizing == null || currentResizing == id).also {
        ctx.dirtyIf(ctx.setState(id, resizeVisibleKey, it))
    }

    if (currentResizing == id) {
        // Currently resizing this window and mouse was moved
        if (ctx.windowState.mouseQueue.lastOrNull()?.let {
                if (it.button == 0 && it.action == GlfwWindow.Action.RELEASE) return@let false
                true
            } == false) {
            ctx.removeState(null, resizingWindowKey)
            ctx.windowState.mouseQueue.removeLast()
            currentResizing = null
        } else {
            size = ctx.windowState.mouse.toVec2f() - position + resizeAnchor!!
            bottomRight = position + size
        }
    }

    val resizeHoverActive = (if (resizeHandleVisible) {
        val (x, y) = mouse
        // Only activate resize handle if we're currently resizing this window, or if nothing is being resized
        if (mouse <= bottomRight) {
            (bottomRight.x - x) + (bottomRight.y - y) <= resizeHandleSize
        } else false
    } else false).also {
        ctx.dirtyIf(ctx.setState(id, resizeActiveKey, it))
    }

    if (currentResizing == null && resizeHoverActive) {
        ctx.windowState.mouseQueue.lastOrNull()?.let {
            if (it.button == 0 && it.action == PRESS) {
                val distFromKnob = bottomRight - ctx.windowState.mouse.toVec2f()
                ctx.setState(null, resizingWindowKey, id to distFromKnob)
                ctx.windowState.mouseQueue.removeLast()
            }
        }
    }

    return resizeHandleVisible
}

fun Layer<Context>.window(title: String = "Kape window", id: ID = title) {
    val ctx = this.context

    val resizeHandleVisible = resizeBehaviour(ctx, id)

    val position = ctx.getState<Vec2f>(id, positionKey)!!
    val size = ctx.getState<Vec2f>(id, sizeKey)!!

    ctx {
        VertexColour {
            push(
                TRIANGLES,
                position.offsets(
                    inclusive = true,   // 0
                    size,               // 1
                    size.justX,         // 2
                    size.justY          // 3
                ),
                intArrayOf(0, 1, 2, 0, 3, 1),
                colour = transparentGrey
            )
        }
    }

    if (resizeHandleVisible) {
        val handleSize = ctx.getState<Float>(id, resizeHandleSizeKey)!!
        val colour = if (ctx.getStateOrPut(id, resizeActiveKey) { false }) {
            blue
        } else {
            blue.copy(a = 128u)
        }
        ctx {
            VertexColour {
                push(
                    TRIANGLES,
                    position.offsets(
                        inclusive = false,
                        size,
                        size - Vec2f(0f, handleSize),
                        size - Vec2f(handleSize, 0f)
                    ),
                    colour = colour
                )
            }
        }
    }

}