package me.zeroeightsix.kape

import me.zeroeightsix.kape.element.layer.ForkOrderedLayer

typealias ID = Any

class Kape() : ForkOrderedLayer() {



}

/**
 * An instance of [Kape] where the default constructor parameters was used.
 *
 * Use this instance if you wish to co-operate with other projects that might be using Kape in the same environment,
 * unless the environment provides an instance of Kape.
 */
val kapeCommon = Kape()