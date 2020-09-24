package me.zeroeightsix.kape.window

import me.zeroeightsix.kape.math.Vec2
import me.zeroeightsix.kape.math.Vec2i
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.system.MemoryUtil.NULL

private fun Boolean.asGlfwBool() = if (this) GLFW_TRUE else GLFW_FALSE

class GlfwWindow private constructor(private val handle: Long) {

    fun freeCallbacks() = glfwFreeCallbacks(this.handle)
    fun destroy() = glfwDestroyWindow(this.handle)

    fun freeAndDestroy() {
        freeCallbacks()
        destroy()
    }

    companion object {
        private fun installCallbacks(handle: Long) {
            glfwSetKeyCallback(handle, glfwKeyCallback { window, key, scancode, action, mods ->
                println("$window: $key#$scancode ($action) mods $mods")
            })
        }

        fun fromHandle(handle: Long, installCallbacks: Boolean = true) =
            GlfwWindow(handle).also { if (installCallbacks) installCallbacks(it.handle) }

        fun createWindow(
            title: String = "me.zeroeightsix.kape.Kape window",
            makeVisible: Boolean = true,
            resizable: Boolean = true,
            size: Vec2i = Vec2(300, 300)
        ): Result<GlfwWindow> {
            if (!glfwInit()) return Result.failure(IllegalStateException("Unable to initialise GLFW"))

            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
            glfwWindowHint(GLFW_RESIZABLE, resizable.asGlfwBool())
            val handle = glfwCreateWindow(size.x, size.y, title, NULL, NULL)

            if (handle == NULL) return Result.failure(IllegalStateException("Failed to create GLFW window!"))

            installCallbacks(handle)

            if (makeVisible) {
                glfwShowWindow(handle)
            }

            return Result.success(GlfwWindow(handle))
        }

        fun terminate() {
            glfwTerminate()
            glfwSetErrorCallback(null)?.free()
        }

        private fun glfwKeyCallback(onInvoke: (Long, Int, Int, Int, Int) -> Unit) = object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                onInvoke(window, key, scancode, action, mods)
            }
        }
    }

}