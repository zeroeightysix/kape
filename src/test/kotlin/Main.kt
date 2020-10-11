import me.zeroeightsix.kape.api.element.window
import me.zeroeightsix.kape.api.kapeCommon
import me.zeroeightsix.kape.impl.gl.KapeGL
import me.zeroeightsix.kape.impl.native.GlfwWindow


fun main() {

    val glfwWindow = GlfwWindow.createWindow {
        resizable = true
        resizeCallback = standardResizeCallback
    }.getOrThrow()

    glfwWindow.makeContextCurrent()
    KapeGL.setUp()
    val kape = kapeCommon

    with (kape) {
        while (!glfwWindow.shouldClose) {
            KapeGL.clear()

            window()

            renderAndRelease()
            glfwWindow.update()
        }
    }

    glfwWindow.freeAndDestroy()

    println("Exiting")
    GlfwWindow.terminateGlfw()

}