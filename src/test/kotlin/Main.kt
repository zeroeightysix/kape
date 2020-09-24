import me.zeroeightsix.kape.window.GlfwWindow

fun main() {

    val window = GlfwWindow.createWindow().getOrThrow()

    Thread.sleep(1000)
    println("Exiting")
    window.destroy()
    GlfwWindow.terminate()

}