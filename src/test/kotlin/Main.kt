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
        resizeCallback = standardResizeCallback
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
    with (kape) {
        while (!glfwWindow.shouldClose) {
            KapeGL.clear()
            kape.nextContext()

            window()

            renderAndRelease()
            glfwWindow.update()
        }
    }

    // Exit
    glfwWindow.freeAndDestroy()
    println("Exiting")
    GlfwWindow.terminateGlfw()

}