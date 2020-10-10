package me.zeroeightsix.kape.api.element

import me.zeroeightsix.kape.api.element.layer.ForkOrderedLayer
import me.zeroeightsix.kape.api.element.layer.Layer

class Window<P>(parent: Layer<P>? = null, context: P) : ForkOrderedLayer<P>(parent, context) {

}