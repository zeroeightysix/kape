package me.zeroeightsix.kape.api.state

interface Reproducible<T: Reproducible<T>> {
    fun createNext(): T
}