package com.donrobo.fpbg.blueprint.building

import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.data.Int2
import net.sf.json.JSONObject

interface Building {
    fun toJson(): JSONObject

    val width: Int

    val height: Int

    val x: Int

    val y: Int

    fun move(x: Int, y: Int): Building

    val name: String

    val position: Int2

    val visualizationCharacter: Char

    fun rotateCCW(around: Int2, count: Int): Building

    fun move(offset: Int, direction: Direction): Building
}
