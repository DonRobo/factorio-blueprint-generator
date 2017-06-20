package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.blueprint.Direction
import com.donrobo.fpbg.blueprint.Direction.*
import com.donrobo.fpbg.blueprint.building.Splitter
import com.donrobo.fpbg.blueprint.building.YellowBelt
import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.data.Int2
import com.donrobo.fpbg.data.PositionalBeltIo

class TwoToOneLayout(val direction: Direction, val item1: String, val item2: String, val rightHanded: Boolean) {

    private val Direction.rotationOffset: Int get() = when (this) {
        UP -> 0
        RIGHT -> 1
        DOWN -> 2
        LEFT -> 3
    }

    val inputs: List<PositionalBeltIo> get() = listOf(
            PositionalBeltIo(
                    position = Int2(0, 3).rotateCCW(count = direction.rotationOffset),
                    item = item1,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCCW(direction.rotationOffset),
                    type = BeltIoType.INPUT
            ),
            PositionalBeltIo(
                    position = Int2(1, 3).rotateCCW(count = direction.rotationOffset),
                    item = item2,
                    beltSide = BeltSide.BOTH,
                    direction = UP.rotateCCW(direction.rotationOffset),
                    type = BeltIoType.INPUT
            )
    )


    val outputs: List<PositionalBeltIo> get() = listOf(
            PositionalBeltIo(
                    position = Int2(0, 0),
                    item = item1,
                    beltSide = BeltSide.LEFT,
                    direction = UP.rotateCCW(direction.rotationOffset),
                    type = BeltIoType.OUTPUT
            ),
            PositionalBeltIo(
                    position = Int2(0, 0),
                    item = item2,
                    beltSide = BeltSide.RIGHT,
                    direction = UP.rotateCCW(direction.rotationOffset),
                    type = BeltIoType.OUTPUT
            )
    )

    fun generateBlueprint(): Blueprint {
        val blueprint = Blueprint()

        if (!rightHanded) TODO()

        /*
         * ^<
         * >^<
         * ^>^
         * SS
         */
        blueprint.addBuilding(YellowBelt(0, 0, UP))
        blueprint.addBuilding(YellowBelt(1, 0, LEFT))
        blueprint.addBuilding(YellowBelt(1, 1, UP))

        blueprint.addBuilding(YellowBelt(0, 1, RIGHT))
        blueprint.addBuilding(YellowBelt(0, 2, UP))

        blueprint.addBuilding(YellowBelt(2, 1, LEFT))
        blueprint.addBuilding(YellowBelt(2, 2, UP))
        blueprint.addBuilding(YellowBelt(1, 2, RIGHT))

        blueprint.addBuilding(Splitter(0, 3, UP))

        return when (direction) {
            UP -> blueprint
            RIGHT -> blueprint.rotateCCW(Int2(0, 0), 1)
            DOWN -> blueprint.rotateCCW(Int2(0, 0), 2)
            LEFT -> blueprint.rotateCCW(Int2(0, 0), 3)
        }
    }

}