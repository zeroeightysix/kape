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
        tieToState(windowState) // Generate callbacks for our window state
    }.getOrThrow()

    // Set up GL
    glfwWindow.makeContextCurrent()
    KapeGL.setUp()

    // Init kape (required to be after context init)
    val kape = kapeCommon
    // Set the kape window state that will be used. Mind that if others are using kapeCommon, that you will be
    // overwriting their window state (or they will be overriding yours)
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