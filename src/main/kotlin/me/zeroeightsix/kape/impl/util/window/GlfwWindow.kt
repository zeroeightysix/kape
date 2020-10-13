package me.zeroeightsix.kape.impl.util.window

import me.zeroeightsix.kape.api.state.BasicWindowState
import me.zeroeightsix.kape.api.state.WindowState
import me.zeroeightsix.kape.api.util.Destroy
import me.zeroeightsix.kape.api.util.math.Vec2i
import me.zeroeightsix.kape.api.util.math.has
import me.zeroeightsix.kape.api.util.window.NativeWindow
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCharCallback
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL11
import org.lwjgl.system.MemoryUtil.NULL
import me.zeroeightsix.kape.api.state.KeyAction as ApiKeyAction
import me.zeroeightsix.kape.api.state.KeyMods as ApiKeyMods

/**
 * A generic key callback. Returns true if the action was handled, and thus any other key callbacks should not take action.
 */
typealias KeyCallback = (window: Long, key: Int, scancode: Int, action: GlfwWindow.KeyAction, mods: GlfwWindow.KeyMods) -> Boolean
typealias ResizeCallback = (window: Long, width: Int, height: Int) -> Unit
typealias CursorPositionCallback = (window: Long, x: Double, y: Double) -> Unit
typealias CharCallback = (window: Long, char: Char) -> Unit

private fun KeyCallback.asGlfwKeyCallback() = object : GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        this@asGlfwKeyCallback(
            window,
            key,
            scancode,
            action.glfwAction ?: error("Invalid GLFW key action"),
            mods.glfwMods
        )
    }
}

private fun ResizeCallback.asGlfwResizeCallback() = object : GLFWFramebufferSizeCallback() {
    override fun invoke(window: Long, width: Int, height: Int) = this@asGlfwResizeCallback(window, width, height)
}

private fun CursorPositionCallback.asGlfwMouseCallback() = object : GLFWCursorPosCallback() {
    override fun invoke(window: Long, xpos: Double, ypos: Double) = this@asGlfwMouseCallback(window, xpos, ypos)
}

private fun CharCallback.asGlfwCharCallback() = object : GLFWCharCallback() {
    override fun invoke(window: Long, codepoint: Int) = this@asGlfwCharCallback(window, codepoint.toChar())
}

private fun Boolean.asGlfwBool() = if (this) GLFW_TRUE else GLFW_FALSE

private val Int.glfwAction
    get() = GlfwWindow.KeyAction.fromInt(this)

private val Int.glfwMods
    get() = GlfwWindow.KeyMods(this)

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class GlfwWindow private constructor(protected val handle: Long) : NativeWindow, Destroy {

    fun freeCallbacks() = glfwFreeCallbacks(this.handle)
    override fun destroy() = glfwDestroyWindow(this.handle)

    private var _shouldClose: Boolean = false

    override val shouldClose: Boolean
        get() = _shouldClose

    override fun update() {
        this._shouldClose = glfwWindowShouldClose(this.handle)

        glfwSwapBuffers(this.handle)
        glfwPollEvents()
    }

    fun makeContextCurrent() {
        glfwMakeContextCurrent(this.handle)
    }

    fun freeAndDestroy() {
        freeCallbacks()
        destroy()
    }

    fun setKeyCallback(callback: KeyCallback) {
        glfwSetKeyCallback(handle, callback.asGlfwKeyCallback())
    }

    fun setResizeCallback(callback: ResizeCallback) {
        glfwSetFramebufferSizeCallback(handle, callback.asGlfwResizeCallback())
    }

    fun setCursorsPosCallback(callback: CursorPositionCallback) {
        glfwSetCursorPosCallback(handle, callback.asGlfwMouseCallback())
    }

    fun setCharCallback(callback: CharCallback) {
        glfwSetCharCallback(handle, callback.asGlfwCharCallback())
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class Builder internal constructor() {
        private var hints = mutableMapOf<Int, Int>()

        /**
         * Don't try to initialise GLFW, or check if it succeeded, when building.
         */
        var noInit = false

        var resizable: Boolean
            get() = hints.getOrDefault(GLFW_RESIZABLE, GLFW_TRUE) == GLFW_TRUE
            set(value) = windowHint(GLFW_RESIZABLE, value.asGlfwBool())

        var size: Vec2i = Vec2i(300, 300)

        var title = "Kape window"

        /**
         * If nonnull, the monitor to create a fullscreen window on. If `null`, creates a windowed window.
         */
        var fullscreenMonitor: Long? = null

        /**
         * The window whose context to share resources with, or `null` to not share resources
         */
        var share: Long? = null

        /**
         * Whether or not to show the window after building
         */
        var show: Boolean = true

        var keyCallback: KeyCallback? = null
        var resizeCallback: ResizeCallback? = null
        val standardResizeCallback: ResizeCallback = { _, w, h ->
            GL11.glViewport(0, 0, w, h)
        }
        var cursorPosCallback: CursorPositionCallback? = null
        var charCallback: CharCallback? = null

        fun defaults(): Builder {
            resizable = true
            return this
        }

        fun windowHint(hint: Int, value: Int) {
            hints[hint] = value
        }

        fun tieToState(state: BasicWindowState) {
            val previousPosCallback = this.cursorPosCallback
            this.cursorPosCallback = { window, x, y ->
                previousPosCallback?.invoke(window, x, y)
                state.setMouse(x, y)
            }

            val previousResizeCallback = this.resizeCallback ?: standardResizeCallback
            this.resizeCallback = { window, w, h ->
                previousResizeCallback(window, w, h)
                state.resize(w, h)
            }

            val previousKeyCallback = this.keyCallback
            this.keyCallback = { window, key, scancode, action, mods ->
                if (previousKeyCallback?.invoke(window, key, scancode, action, mods) != true) {
                    state.pushKeyEvent(
                        WindowState.KeyEvent(
                            key,
                            scancode,
                            action.api,
                            mods
                        )
                    )
                }
                false
            }

            val previousCharCallback = this.charCallback
            this.charCallback = { window, char ->
                previousCharCallback?.invoke(window, char)
                state.pushChar(char)
            }
        }

        fun build(): Result<GlfwWindow> {
            if (!noInit && !glfwInit()) return Result.failure(IllegalStateException("Unable to initialise GLFW"))

            // Do not show the window until it's fully set up
            windowHint(GLFW_VISIBLE, false.asGlfwBool())
            // Apply all other hints. The user may decide to add an entry for GLFW_VISIBLE, to override the behaviour written above.
            for ((hint, value) in hints)
                glfwWindowHint(hint, value)

            val handle =
                glfwCreateWindow(size.x, size.y, title, fullscreenMonitor.asGlfwNullable(), share.asGlfwNullable())
            if (handle == NULL) return Result.failure(IllegalStateException("Failed to create GLFW window"))

            val window = GlfwWindow(handle)

            this.keyCallback?.run { window.setKeyCallback(this) }
            this.resizeCallback?.run { window.setResizeCallback(this) }
            this.cursorPosCallback?.run { window.setCursorsPosCallback(this) }
            this.charCallback?.run { window.setCharCallback(this) }

            if (show)
                glfwShowWindow(handle)

            return Result.success(window)
        }

        private fun Long?.asGlfwNullable() = this ?: NULL

    }

    @Suppress("unused")
    enum class KeyAction(val glfw: Int, val api: ApiKeyAction) {
        PRESS(GLFW_PRESS, ApiKeyAction.PRESS),
        RELEASE(GLFW_RELEASE, ApiKeyAction.RELEASE),
        REPEAT(GLFW_REPEAT, ApiKeyAction.REPEAT);

        companion object {
            private val actionMap = values().map { it.glfw to it }.toMap()

            fun fromInt(int: Int): KeyAction? = actionMap[int]
        }
    }

    data class KeyMods(
        override val shift: Boolean,
        override val control: Boolean,
        override val alt: Boolean,
        override val `super`: Boolean,
        override val capsLock: Boolean,
        override val numLock: Boolean
    ) : ApiKeyMods(shift, control, alt, `super`, capsLock, numLock) {
        constructor(glfw: Int) : this(
            glfw has GLFW_MOD_SHIFT,
            glfw has GLFW_MOD_CONTROL,
            glfw has GLFW_MOD_ALT,
            glfw has GLFW_MOD_SUPER,
            glfw has GLFW_MOD_CAPS_LOCK,
            glfw has GLFW_MOD_NUM_LOCK
        )
    }

    @Suppress("unused")
    companion object {
        fun fromHandle(handle: Long) = GlfwWindow(handle)

        fun createWindow(options: Builder.() -> Unit = {}) = Builder().defaults().also(options).build()

        fun terminateGlfw() {
            glfwTerminate()
            glfwSetErrorCallback(null)?.free()
        }
    }

}

