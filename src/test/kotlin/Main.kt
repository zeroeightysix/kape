import me.zeroeightsix.kape.api.destroyAll
import me.zeroeightsix.kape.api.element.lineTo
import me.zeroeightsix.kape.api.element.window
import me.zeroeightsix.kape.api.kapeCommon
import me.zeroeightsix.kape.api.math.Vec2f
import me.zeroeightsix.kape.impl.gl.KapeGL
import me.zeroeightsix.kape.impl.gl.VAO
import me.zeroeightsix.kape.impl.gl.VBO
import me.zeroeightsix.kape.impl.gl.standardProgram
import me.zeroeightsix.kape.impl.native.GlfwWindow
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL20


fun main() {

    val window = GlfwWindow.createWindow {
        resizable = true
        keyCallback = { _, _, _, _, _->
            println("Key callback called")
            false // Action was not consumed
        }
        resizeCallback = standardResizeCallback
    }.getOrThrow()

    window.makeContextCurrent()
    KapeGL.setUp()
    val kape = kapeCommon
    val program = standardProgram

    // set up vertices
    with (kape) {
        while (!window.shouldClose) {
            KapeGL.clear()

            window()

            this.renderAndRelease()
            this.children.clear()
            window.update()
        }
    }

    window.freeCallbacks()
    destroyAll(window)

    println("Exiting")
    GlfwWindow.terminate()

}