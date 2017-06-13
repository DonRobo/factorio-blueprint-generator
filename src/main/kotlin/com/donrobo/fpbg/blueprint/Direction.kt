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

    companion object {
        fun calculateDirection(from: Int2, to: Int2): Direction {
            assert((from.x == to.x) != (from.y == to.y))
            if (from.x < to.x) {
                return Direction.RIGHT
            } else if (from.x > to.x) {
                return Direction.LEFT
            } else if (from.y < to.y) {
                return Direction.DOWN
            } else if (from.y > to.y) {
                return Direction.UP
            } else {
                throw RuntimeException("Shouldn't happen")
            }
        }
    }
}
