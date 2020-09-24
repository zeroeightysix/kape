import me.zeroeightsix.kape.window.GlfwWindow

fun main() {

    val window = GlfwWindow.createWindow {
        resizable = false
        keyCallback = { l: Long, i: Int, i1: Int, i2: Int, i3: Int ->
            println("extra callback")
            false
        }
    }.getOrThrow()

    Thread.sleep(5000)
    println("Exiting")
    window.destroy()
    GlfwWindow.terminate()

}