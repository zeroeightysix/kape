package me.zeroeightsix.kape

/**
 * An exception to indicate that an unreachable code was reached: often used for an illegal access
 */
public class UnreachableError(message: String = "An operation is not accessible.") : Error(message)

fun unreachable(): Nothing = throw UnreachableError()

class Kape {



}