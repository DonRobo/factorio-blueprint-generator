package com.donrobo.fpbg.util

import com.donrobo.fpbg.data.Int2

class Map2D {

    private val map = HashMap<Int2, Char>()

    val minimumX: Int
        get() = map.map { it.key.x }.min() ?: 0

    val maximumX: Int
        get() = map.map { it.key.x }.max() ?: 0

    val minimumY: Int
        get() = map.map { it.key.y }.min() ?: 0

    val maximumY: Int
        get() = map.map { it.key.y }.max() ?: 0

    val width: Int
        get() = if (map.isEmpty()) 0 else maximumX - minimumX + 1//1-1 is still one width

    val height: Int
        get() = if (map.isEmpty()) 0 else maximumY - minimumY + 1//1-1 is still one height

    operator fun set(pos: Int2, value: Char) {
        map.put(pos, value)
    }

    operator fun set(x: Int, y: Int, value: Char) {
        map.put(Int2(x, y), value)
    }

    operator fun get(pos: Int2): Char? {
        return map[pos]
    }

    operator fun get(x: Int, y: Int): Char? {
        return map[Int2(x, y)]
    }

    fun visualize(showCoordinates: Boolean = false): String { //TODO schöner machen
        if (map.isEmpty()) return ""

        val stringBuilder = StringBuilder()

        val coordinateSize = 4

        val maximumYToUse = maximumY + if (showCoordinates) coordinateSize else 0
        for (y in minimumY..maximumYToUse) {
            val minimumXToUse = if (showCoordinates) minimumX - coordinateSize else minimumX
            for (x in minimumXToUse..maximumX) {
                if (y > maximumY && x < minimumX)
                    stringBuilder.append(' ')
                else if (y > maximumY)
                    stringBuilder.append(x.toString().padEnd(coordinateSize, ' ')[y - maximumY - 1])
                else if (x < minimumX)
                    stringBuilder.append(y.toString().padStart(coordinateSize, ' ')[coordinateSize - (minimumX - x)])
                else
                    stringBuilder.append(map[Int2(x, y)] ?: ' ')
            }
            stringBuilder.append("\n")
        }

        stringBuilder.deleteCharAt(stringBuilder.length - 1) //remove last \n

        return stringBuilder.toString()
    }

    fun asMap(): Map<Int2, Char> = HashMap(map)

}

fun String.asMap(emptyChar: Char = ' '): Map2D {
    val lines = lines()
    val width = lines.map { it.length }.max() ?: 0
    val height = if (width > 0) lines.size else 0

    val map = Map2D()

    lines.forEachIndexed { y, line ->
        for (x in 0..(width - 1)) {
            if (x < line.length) {
                map[x, y] = line[x]
            } else {
                map[x, y] = emptyChar
            }
        }
    }

    assert(map.width == width)
    assert(map.height == height)

    return map
}