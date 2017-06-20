package com.donrobo.fpbg.blueprint.building

import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.data.Int2
import net.sf.json.JSONObject

abstract class AbstractBuilding(override val x: Int, override val y: Int) : Building {

    override fun toJson(): JSONObject {
        val json = JSONObject()

        json.put("name", name)
        val posJson = JSONObject()
        posJson.put("x", x + blueprintXOffset)
        posJson.put("y", y + blueprintYOffset)
        json.put("position", posJson)

        addCustomPropertiesToJson(json)

        return json
    }

    protected open fun addCustomPropertiesToJson(json: JSONObject) {}

    protected abstract val blueprintXOffset: Double

    protected abstract val blueprintYOffset: Double

    override val position: Int2
        get() = Int2(x, y)

    override fun move(offset: Int, direction: Direction): Building {
        val (x1, y1) = direction.move(Int2(0, 0), offset)

        return move(x1, y1)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as AbstractBuilding

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}
