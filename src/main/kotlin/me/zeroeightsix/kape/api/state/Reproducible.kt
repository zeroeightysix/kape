package me.zeroeightsix.kape.api.state

interface Reproducible<T : Reproducible<T, R>, R> {
    fun createNext(requirements: R): T
}