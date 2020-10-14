package me.zeroeightsix.kape.api.state

import me.zeroeightsix.kape.api.util.math.Vec2d
import me.zeroeightsix.kape.api.util.math.Vec2i

interface WindowState {
    val mouse: Vec2d
    val mouseDelta: Vec2d
    val charQueue: ArrayDeque<Char>
    val keyQueue: ArrayDeque<KeyEvent>
    val size: Vec2i

    fun clear()

    data class KeyEvent(val key: Int, val scancode: Int, val action: KeyAction, val mods: KeyMods)
}

enum class KeyAction {
    PRESS, RELEASE, REPEAT
}

@Suppress("LeakingThis", "CanBeParameter")
open class KeyMods(
    open val shift: Boolean,
    open val control: Boolean,
    open val alt: Boolean,
    open val `super`: Boolean,
    open val capsLock: Boolean,
    open val numLock: Boolean
) {
    // Common combination getters
    val isControlAlt: Boolean = control && alt
    val isControlAltShift: Boolean = isControlAlt && shift
}