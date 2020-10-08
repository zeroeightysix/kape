import me.zeroeightsix.kape.gl.KapeGL
import me.zeroeightsix.kape.kapeCommon
import me.zeroeightsix.kape.native.GlfwWindow
import me.zeroeightsix.kape.window

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
    val kape = kapeCommon
    while (!window.shouldClose) {
        KapeGL.clear()
        
        kape.window {

        }

        window.update()
    }

    println("Exiting")
    window.freeAndDestroy()
    GlfwWindow.terminate()

}