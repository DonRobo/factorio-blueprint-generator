package com.donrobo.fpbg.blueprint.building

import com.donrobo.fpbg.data.Int2
import net.sf.json.JSONObject

class AssemblingMachine2(val recipe: String, x: Int, y: Int) : AbstractBuilding(x, y) {

    override val name: String
        get() = "assembling-machine-2"

    override val visualizationCharacter: Char
        get() = '2'

    override val width: Int
        get() = 3

    override val height: Int
        get() = 3

    override fun move(x: Int, y: Int): AssemblingMachine2 {
        return AssemblingMachine2(recipe, this.x + x, this.y + y)
    }

    override val blueprintXOffset: Double
        get() = 1.0
    override val blueprintYOffset: Double
        get() = 1.0

    override fun addCustomPropertiesToJson(json: JSONObject) {
        json.put("recipe", recipe)
    }

    override fun rotateCCW(around: Int2, count: Int): Building {
        TODO("Untested")
//        val newPosition = position.rotateCCW(around, count) //probably some magic needed here
//
//        return AssemblingMachine2(recipe, newPosition.x, newPosition.y)
    }

    companion object {

        val craftingSpeed: Double
            get() = 0.75
    }
}
