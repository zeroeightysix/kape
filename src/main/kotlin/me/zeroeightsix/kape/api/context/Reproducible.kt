package me.zeroeightsix.kape.api.context

interface Reproducible<T: Reproducible<T>> {
    fun createNext(): T
}