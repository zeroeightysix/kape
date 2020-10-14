import me.zeroeightsix.kape.api.Kape
import me.zeroeightsix.kape.api.destroyAll
import me.zeroeightsix.kape.api.state.BasicWindowState
import me.zeroeightsix.kape.api.state.Context
import me.zeroeightsix.kape.impl.render.KapeGL
import me.zeroeightsix.kape.impl.render.renderer.GlLayerRenderer
import me.zeroeightsix.kape.impl.util.window.GlfwWindow
import me.zeroeightsix.kape.impl.widget.window

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
    val kape = Kape(windowState, GlLayerRenderer(), Context(windowState))
    // Set the kape window state that will be used. Mind that if others are using kapeCommon, that you will be
    // overwriting their window state (or they will be overriding yours)
    kape.windowState = windowState

    // Main loop
    while (!glfwWindow.shouldClose) {
        // Poll events & state etc
        glfwWindow.update()

        // Clear screen
        KapeGL.clear()

        // Compose the kape frame & render it
        kape.frame {
            window()
        }
    }

    // Clean up resources
    glfwWindow.freeCallbacks()
    destroyAll(glfwWindow, kape)
    // Exit
    println("Exiting")
    GlfwWindow.terminateGlfw()

}