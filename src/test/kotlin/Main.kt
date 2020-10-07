import me.zeroeightsix.kape.gl.KapeGL
import me.zeroeightsix.kape.window.GlfwWindow

fun main() {

    val window = GlfwWindow.createWindow {
        resizable = false
        keyCallback = { _, _, _, _, _->
            println("Key callback called")
            false // Action was not consumed
        }
    }.getOrThrow()

    window.makeContextCurrent()
    KapeGL.setUp()
    while (!window.shouldClose) {
        KapeGL.clear()

        window.update()
    }

    println("Exiting")
    window.freeAndDestroy()
    GlfwWindow.terminate()

}