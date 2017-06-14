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
}