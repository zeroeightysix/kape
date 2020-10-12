package me.zeroeightsix.kape.api.context

import me.zeroeightsix.kape.api.math.Vec2d
import me.zeroeightsix.kape.api.math.Vec2i

class BasicWindowState : WindowState {
    private var _mouse = Vec2d(-1.0, -1.0)
    private var _charQueue = ArrayDeque<Char>()
    private var _size: Vec2i = Vec2i(0, 0)

    override val mouse
        get() = _mouse
    override val charQueue
        get() = _charQueue
    override val size
        get() = _size

    fun setMouse(mouse: Vec2d) {
        this._mouse = mouse
    }

    fun setMouse(x: Double, y: Double) = setMouse(Vec2d(x, y))

    fun pushChar(char: Char) {
        this.charQueue.add(char)
    }

    fun pushChars(vararg chars: Char) = chars.forEach(this::pushChar)

    fun resize(size: Vec2i) {
        this._size = size
    }

    fun resize(w: Int, h: Int) = resize(Vec2i(w, h))

    override fun clear() {
        this._charQueue.clear()
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

    override fun clear() = Unit
}