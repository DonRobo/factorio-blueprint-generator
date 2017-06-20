package com.donrobo.fpbg.blueprint.building

import com.donrobo.fpbg.data.Int2
import net.sf.json.JSONObject

class AssemblingMachine1(val recipe: String, x: Int, y: Int) : AbstractBuilding(x, y) {

    override val width: Int
        get() = 3

    override val height: Int
        get() = 3

    override fun move(x: Int, y: Int): AssemblingMachine1 {
        return AssemblingMachine1(recipe, this.x + x, this.y + y)
    }

    override val name: String
        get() = "assembling-machine-1"

    override val visualizationCharacter: Char
        get() = '1'

    override fun addCustomPropertiesToJson(json: JSONObject) {
        json.put("recipe", recipe)
    }

    override val blueprintXOffset: Double
        get() = 1.0
    override val blueprintYOffset: Double
        get() = 1.0

    override fun rotateCCW(around: Int2, count: Int): Building {
        TODO("Untested")
//        val newPosition = position.rotateCCW(around, count) //probably some magic needed here
//
//        return AssemblingMachine1(recipe, newPosition.x, newPosition.y)
    }
}
