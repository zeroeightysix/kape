package me.zeroeightsix.kape.api.state

import me.zeroeightsix.kape.api.state.WindowState.KeyEvent
import me.zeroeightsix.kape.api.state.WindowState.MouseEvent
import me.zeroeightsix.kape.api.util.math.Vec2d
import me.zeroeightsix.kape.api.util.math.Vec2i
import me.zeroeightsix.kape.api.util.math.minus

@Suppress("MemberVisibilityCanBePrivate", "unused")
class BasicWindowState : WindowState {
    private val noMouse = Vec2d(-1.0)
    private var _mouse = noMouse
    private val noMouseDelta = Vec2d(0.0)
    private var _mouseDelta = noMouseDelta
    private var _size: Vec2i = Vec2i(0, 0)

    override val charQueue = ArrayDeque<Char>()
    override val keyQueue = ArrayDeque<KeyEvent>()
    override val mouseQueue = ArrayDeque<MouseEvent>()
    override val mouse
        get() = _mouse
    override val mouseDelta: Vec2d
        get() = _mouseDelta
    override val size
        get() = _size

    fun setMouse(mouse: Vec2d) {
        if (this._mouse != noMouse)
            this._mouseDelta = mouse - this._mouse
        this._mouse = mouse
    }

    fun setMouse(x: Double, y: Double) = setMouse(Vec2d(x, y))

    fun pushChar(char: Char) {
        this.charQueue.add(char)
    }

    fun pushKeyEvent(event: KeyEvent) {
        this.keyQueue.add(event)
    }

    fun pushMouseEvent(event: MouseEvent) {
        this.mouseQueue.add(event)
    }

    fun pushChars(vararg chars: Char) = chars.forEach(this::pushChar)

    fun resize(size: Vec2i) {
        this._size = size
    }

    fun resize(w: Int, h: Int) = resize(Vec2i(w, h))

    override fun clear() {
        this._mouseDelta = noMouseDelta
        this.charQueue.clear()
        this.keyQueue.clear()
        this.mouseQueue.clear()
    }
}

object UninitialisedWindowState : WindowState {
    private fun error(): Nothing = error("Uninitialised window state, try setting the kape window state instance.")

    override val size: Vec2i
        get() = error()
    override val charQueue: ArrayDeque<Char>
        get() = error()
    override val mouse: Vec2d
        get() = error()
    override val mouseDelta: Vec2d
        get() = error()
    override val keyQueue: ArrayDeque<KeyEvent>
        get() = error()
    override val mouseQueue: ArrayDeque<MouseEvent>
        get() = error()

    override fun clear() = Unit
}