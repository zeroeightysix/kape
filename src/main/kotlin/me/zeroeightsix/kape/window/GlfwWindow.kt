package me.zeroeightsix.kape.window

import me.zeroeightsix.kape.math.Vec2i
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.system.MemoryUtil.NULL

/**
 * A generic key callback. Returns true if the action was handled, and thus any other key callbacks should not take action.
 *
 * Arguments: window, key, scancode, action, mods
 */
typealias KeyCallback = (Long, Int, Int, Int, Int) -> Boolean

private fun KeyCallback.asGlfwKeyCallback() = object : GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        this@asGlfwKeyCallback(window, key, scancode, action, mods)
    }
}

private fun Boolean.asGlfwBool() = if (this) GLFW_TRUE else GLFW_FALSE

open class GlfwWindow private constructor(protected val handle: Long) : Window {

    fun freeCallbacks() = glfwFreeCallbacks(this.handle)
    fun destroy() = glfwDestroyWindow(this.handle)

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

    private val keyCallback: KeyCallback = { window, key, scancode, action, mods ->
        println("$window: $key#$scancode ($action) mods $mods")
        false
    }

    private fun wrapKeyCallback(priorityCallback: KeyCallback? = null): KeyCallback = priorityCallback?.let {
        { window, key, scancode, action, mods ->
            if (priorityCallback(window, key, scancode, action, mods)) true else this.keyCallback(window, key, scancode, action, mods)
        }
    } ?: this.keyCallback

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

        /**
         * An *additional* key callback. The standard Kape key callback is always added, with a lower priority than this one, if not null.
         */
        var keyCallback: KeyCallback? = null

        fun defaults(): Builder {
            resizable = true
            return this
        }

        fun windowHint(hint: Int, value: Int) {
            hints[hint] = value
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

            glfwSetKeyCallback(handle, window.wrapKeyCallback(this.keyCallback).asGlfwKeyCallback())

            if (show)
                glfwShowWindow(handle)

            return Result.success(window)
        }

        private fun Long?.asGlfwNullable() = this ?: NULL

    }

    companion object {
        fun fromHandle(handle: Long, installCallbacks: Boolean = true) =
            GlfwWindow(handle).also { if (installCallbacks) glfwSetKeyCallback(it.handle, it.keyCallback.asGlfwKeyCallback()) }

        fun createWindow(options: Builder.() -> Unit = {}) = Builder().defaults().also(options).build()

        fun terminate() {
            glfwTerminate()
            glfwSetErrorCallback(null)?.free()
        }
    }

}

