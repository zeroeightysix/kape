import me.zeroeightsix.kape.api.gl.KapeGL
import me.zeroeightsix.kape.api.kapeCommon
import me.zeroeightsix.kape.impl.native.GlfwWindow
import me.zeroeightsix.kape.api.window

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