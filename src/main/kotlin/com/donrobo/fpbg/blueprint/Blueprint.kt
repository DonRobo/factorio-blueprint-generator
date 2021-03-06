package com.donrobo.fpbg.blueprint

import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.building.Building
import com.donrobo.fpbg.blueprint.building.YellowBelt
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.util.Map2D
import com.donrobo.fpbg.util.asMap
import net.sf.json.JSONArray
import net.sf.json.JSONObject
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.zip.DeflaterOutputStream

class Blueprint {

    private val internalBuildings = ArrayList<Building>()

    fun toJson(): JSONObject {
        val json = JSONObject()

        val blueprintJson = JSONObject()
        val entitiesArray = JSONArray()

        var i = 0
        for (building in buildings) {
            i++
            val buildingJson = building.toJson()
            buildingJson.put("entity_number", i)
            entitiesArray.add(buildingJson)
        }

        blueprintJson.put("entities", entitiesArray)
        json.put("blueprint", blueprintJson)
        json.put("item", "blueprint")

        return json
    }

    fun toBlueprintString(): String {
        return toBlueprintString(toJson().toString())
    }

    fun addBuilding(building: Building) {
        if (isOccupied(building)) {
            throw RuntimeException("Trying to place building on top of building!")
        }
        internalBuildings.add(building)
    }

    fun isOccupied(pos: Int2) = isOccupied(pos.x, pos.y)

    fun isOccupied(x: Int, y: Int) = get(x, y) != null

    fun isOccupied(x: Int, y: Int, width: Int, height: Int): Boolean {
        for (xI in x..(x + width - 1)) {
            (y..y + height - 1).filter { isOccupied(xI, it) }.forEach { return true }
        }

        return false
    }

    fun isOccupied(building: Building) = isOccupied(building.x, building.y, building.width, building.height)

    operator fun get(x: Int, y: Int): Building? = buildings.filter { building ->
        val xFits = building.x <= x && building.x + building.width - 1 >= x
        val yFits = building.y <= y && building.y + building.height - 1 >= y

        xFits && yFits
    }.firstOrNull()

    fun remove(x: Int, y: Int): Building? {
        val building = get(x, y)
        if (building != null) {
            internalBuildings.remove(building)
        }
        return building
    }

    fun addBlueprint(blueprint: Blueprint, xOffset: Int = 0, yOffset: Int = 0) {
        blueprint.buildings.map { b -> b.move(xOffset, yOffset) }.forEach { this.addBuilding(it) }
    }

    fun move(xOffset: Int, yOffset: Int): Blueprint {
        val blueprint = Blueprint()

        blueprint.addBlueprint(this, xOffset, yOffset)

        return blueprint
    }

    val minimumX: Int
        get() = buildings.map { it.x }.min() ?: 0

    val maximumX: Int
        get() = buildings.map { it.x + it.width - 1 }.max() ?: 0

    val minimumY: Int
        get() = buildings.map { it.y }.min() ?: 0

    val maximumY: Int
        get() = buildings.map { it.y + it.height - 1 }.max() ?: 0

    val width: Int
        get() = maximumX - minimumX + 1//1-1 is still one width

    val height: Int
        get() = maximumY - minimumY + 1//1-1 is still one height

    fun canPlace(blueprint: Blueprint, x: Int, y: Int): Boolean {
        return blueprint.buildings.none { isOccupied(it.move(x, y)) }
    }

    val buildings: List<Building> get() = internalBuildings

    operator fun get(pos: Int2): Building? {
        return get(pos.x, pos.y)
    }

    fun visualizer(): Map2D {
        val mapVisualizer = Map2D()

        for (building in buildings) {
            for (y in building.y..(building.y + building.height - 1)) {
                for (x in building.x..(building.x + building.width - 1)) {
                    mapVisualizer[Int2(x, y)] = building.visualizationCharacter
                }
            }
        }

        return mapVisualizer
    }

    companion object {

        fun toBlueprintString(input: String): String {
            var baos: ByteArrayOutputStream? = null
            var dos: DeflaterOutputStream? = null

            try {
                baos = ByteArrayOutputStream()
                dos = DeflaterOutputStream(baos)

                dos.write(input.toByteArray(Charset.forName("UTF-8")))
                IOUtils.closeQuietly(dos)

                return "0" + Base64.getEncoder().encodeToString(baos.toByteArray())
            } catch (ex: IOException) {
                throw RuntimeException("Shouldn't happen here", ex)
            } finally {
                IOUtils.closeQuietly(baos)
                IOUtils.closeQuietly(dos)
            }
        }
    }

    fun visualize(showCoordinates: Boolean = false): String {
        return visualizer().visualize(showCoordinates)
    }

    fun rotateCW(around: Int2 = Int2(0, 0), count: Int): Blueprint {
        val rotated = Blueprint()

        for (building in buildings) {
            rotated.addBuilding(building.rotateCW(around, count))
        }

        return rotated
    }
}

fun String.toBeltBlueprint(): Blueprint {
    return asMap().toBeltBlueprint()
}

fun Map2D.toBeltBlueprint(): Blueprint {
    val blueprint = Blueprint()

    asMap().forEach { (pos, char) ->
        if (char != ' ')
            blueprint.addBuilding(when (char) {
                '^' -> YellowBelt(pos.x, pos.y, UP)
                '>' -> YellowBelt(pos.x, pos.y, RIGHT)
                'v' -> YellowBelt(pos.x, pos.y, DOWN)
                '<' -> YellowBelt(pos.x, pos.y, LEFT)
                else -> throw IllegalArgumentException("$char is not supported")
            })

    }

    assert(width == blueprint.width)
    assert(height == blueprint.height)

    return blueprint
}
