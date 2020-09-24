import me.zeroeightsix.kape.gl.KapeGL
import me.zeroeightsix.kape.window.GlfwWindow

fun main() {

    val window = GlfwWindow.createWindow {
        resizable = false
        keyCallback = { l: Long, i: Int, i1: Int, i2: Int, i3: Int ->
            println("extra callback")
            false
        }
    }.getOrThrow()

    window.makeContextCurrent()
    KapeGL.setUp()
    while (!window.shouldClose) {
        KapeGL.clear()

        window.update()
    }

    println("Exiting")
    window.destroy()
    GlfwWindow.terminate()

}