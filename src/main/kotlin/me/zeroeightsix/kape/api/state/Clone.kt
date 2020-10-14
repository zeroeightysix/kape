package me.zeroeightsix.kape.api.state

interface Clone<T : Clone<T>> {

    fun clone(): T

}