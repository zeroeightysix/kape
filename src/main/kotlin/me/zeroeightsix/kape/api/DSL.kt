package me.zeroeightsix.kape.api

import me.zeroeightsix.kape.api.state.layer.Layer
import me.zeroeightsix.kape.api.util.Destroy

internal fun <P, T : Layer<P>> Layer<P>.forkAndScope(child: T, id: ID, block: T.() -> Unit) {
    this.fork(child, id)
    child.block()
}

fun destroyAll(vararg toDestroy: Destroy) = toDestroy.forEach(Destroy::destroy)