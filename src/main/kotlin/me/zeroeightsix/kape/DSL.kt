package me.zeroeightsix.kape

import me.zeroeightsix.kape.element.Window
import me.zeroeightsix.kape.element.layer.Layer

internal fun <T : Layer> Layer.forkAndScope(child: T, id: ID, block: T.() -> Unit) {
    this.fork(child, id)
    child.block()
}

fun Layer.window(title: String = "Kape window", id: ID = title, block: Window.() -> Unit) =
    forkAndScope(Window(this), id, block)