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
//    val kape = kapeCommon

    val vertices = floatArrayOf(
        -0.5f, -0.5f, 0.0f,  // left
        0.5f, -0.5f, 0.0f,  // right
        0.0f, 0.5f, 0.0f // top
    )

    val vao = VAO()
    val vbo = VBO()
    val program = standardProgram

    // set up vertices
    with (kapeCommon) {
        vao.bindScoped {
            vbo.bindScoped {
                GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW)

                GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0)
                GL20.glEnableVertexAttribArray(0)
            }
        }

        while (!window.shouldClose) {
            KapeGL.clear()

            program.bindScoped {
                vao.bindScoped {
                    GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3)
                }
            }

            window()

            this.render()
            this.children.clear()
            window.update()
        }
    }

    window.freeCallbacks()
    destroyAll(vao, vbo, program, window)

    println("Exiting")
    GlfwWindow.terminate()

}