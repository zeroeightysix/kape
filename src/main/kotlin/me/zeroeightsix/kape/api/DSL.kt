package me.zeroeightsix.kape.api

import me.zeroeightsix.kape.api.element.Window
import me.zeroeightsix.kape.api.element.layer.Layer

internal fun <P, T : Layer<P>> Layer<P>.forkAndScope(child: T, id: ID, block: T.() -> Unit) {
    this.fork(child, id)
    child.block()
}

fun <P> Layer<P>.window(title: String = "Kape window", id: ID = title, block: Window<P>.() -> Unit) =
    forkAndScope(Window(this), id, block)

fun destroyAll(vararg toDestroy: Destroy) = toDestroy.forEach(Destroy::destroy)