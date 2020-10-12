import me.zeroeightsix.kape.api.context.BasicWindowState
import me.zeroeightsix.kape.api.element.window
import me.zeroeightsix.kape.api.kapeCommon
import me.zeroeightsix.kape.api.math.Vec2d
import me.zeroeightsix.kape.impl.gl.KapeGL
import me.zeroeightsix.kape.impl.native.GlfwWindow

fun main() {

    val windowState = BasicWindowState()

    // Create GLFW window
    val glfwWindow = GlfwWindow.createWindow {
        resizable = true
        resizeCallback = { window, w, h ->
            standardResizeCallback(window, w, h)
            windowState.resize(w, h)
        }
        cursorPosCallback = { _, x, y ->
            windowState.setMouse(x, y)
        }
    }.getOrThrow()

    // Set up GL
    glfwWindow.makeContextCurrent()
    KapeGL.setUp()

    // Init kape (required to be after context init)
    val kape = kapeCommon
    kape.windowState = windowState

    // Main loop
    while (!glfwWindow.shouldClose) {
        // Clear screen
        KapeGL.clear()

        // Compose the kape frame & render it
        kape.frame {
            window()
        }

        // Poll events & state etc
        glfwWindow.update()
    }

    // Exit
    glfwWindow.freeAndDestroy()
    println("Exiting")
    GlfwWindow.terminateGlfw()

}