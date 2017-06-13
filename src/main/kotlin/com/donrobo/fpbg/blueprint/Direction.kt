package com.donrobo.fpbg.blueprint

import com.donrobo.fpbg.data.Int2

enum class Direction(val directionValue: Int) {

    UP(4),
    DOWN(0),
    LEFT(2),
    RIGHT(6);

    val reversed: Direction get() = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
        else -> throw RuntimeException("New direction added?")
    }

    fun move(start: Int2, distance: Int = 1): Int2 {
        var actualX = start.x
        var actualY = start.y

        when (this) {
            UP -> actualY -= distance
            DOWN -> actualY += distance
            LEFT -> actualX -= distance
            RIGHT -> actualX += distance
        }

        return Int2(actualX, actualY)
    }
}
