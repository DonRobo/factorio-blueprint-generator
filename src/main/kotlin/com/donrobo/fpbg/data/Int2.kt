package com.donrobo.fpbg.data

data class Int2(val x: Int, val y: Int) {
    override fun toString(): String {
        return "[$x/$y]"
    }

    operator fun plus(other: Int2): Int2 {
        return Int2(x + other.x, y + other.y)
    }

    operator fun minus(other: Int2): Int2 {
        return Int2(x - other.x, y - other.y)
    }

    fun rotateCCW(around: Int2, count: Int): Int2 {
        val newX = when (count) {
            1 -> -(y - around.y)
            2 -> -(x - around.x)
            3 -> (y - around.y)
            else -> throw IllegalArgumentException("Only count 1-3 are allowed")
        } + around.x
        val newY: Int = when (count) {
            1 -> (x - around.x)
            2 -> -(y - around.y)
            3 -> -(x - around.x)
            else -> throw IllegalArgumentException("Only count 1-3 are allowed")
        } + around.y

        return Int2(newX, newY)
    }
}