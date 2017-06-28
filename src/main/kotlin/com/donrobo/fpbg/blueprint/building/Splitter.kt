package com.donrobo.fpbg.blueprint.building

import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.data.Int2
import net.sf.json.JSONObject

class Splitter(x: Int, y: Int, val direction: Direction) : AbstractBuilding(x, y) {

    override val width: Int
        get() = if (direction === UP || direction === DOWN) 2 else 1

    override val height: Int
        get() = if (direction === UP || direction === DOWN) 1 else 2

    override fun move(x: Int, y: Int): Building {
        return Splitter(this.x + x, this.y + y, direction)
    }

    override val name: String
        get() = "splitter"

    override val visualizationCharacter: Char
        get() = 'S'

    override val blueprintXOffset: Double
        get() = if (direction === UP || direction === DOWN) 0.5 else 0.0
    override val blueprintYOffset: Double
        get() = if (direction === LEFT || direction === RIGHT) 0.5 else 0.0

    override fun addCustomPropertiesToJson(json: JSONObject) {
        json.put("direction", direction.reversed.directionValue)
    }

    override fun rotateCW(around: Int2, count: Int): Building {
        val newDirection = direction.rotateCW(count)

        val newPosition = position.rotateCW(around, count) + when (direction) {
            UP, RIGHT -> when (newDirection) {
                DOWN -> Int2(-1, 0)
                LEFT -> Int2(0, -1)
                else -> Int2(0, 0)
            }
            DOWN, LEFT -> when (newDirection) {
                UP -> Int2(-1, 0)
                RIGHT -> Int2(0, -1)
                else -> Int2(0, 0)
            }
        }

        return Splitter(newPosition.x, newPosition.y, newDirection)
    }
}
